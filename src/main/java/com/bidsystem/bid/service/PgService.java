package com.bidsystem.bid.service;

import com.bidsystem.bid.service.PgCommon.*;
import com.bidsystem.bid.mapper.PaymentMapper;
import com.bidsystem.bid.mapper.BidMapper;
import com.bidsystem.bid.service.ExceptionService.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PgService {
    private static final Logger logger = LoggerFactory.getLogger(BidService.class); 

    @Autowired
    private PgCommon pgInterfaceCommon;

    @Autowired
    private UserService userService;

    @Autowired
    private BidMapper bidMapper;

    @Autowired
    private PaymentMapper paymentMapper;

    private AtomicInteger counter = new AtomicInteger(1);

    public String pgStartPost(Map<String, Object> request, HttpServletRequest request2, HttpServletResponse response) {
   
            try {
                // 전화번호 유효성 검사
                // pgInterfaceCommon.validateRequestParameters(request);
            
                // 결제요청에 필요한 사용자 DB정보 요청
                System.out.println("Step 1: 전화번호 유효성 검사 및 사용자 정보 요청 시작");
                request.put("queryType", "telno");
                request.put("query", request.get("telno"));
                System.out.println("Step 1: request"+request);
                System.out.println("Step 1: telno"+request.get("telno"));
                Map<String, Object> userInfo = userService.getUserByQuery(request);
            
                if (userInfo == null || userInfo.isEmpty()) {
                    System.out.println("Step 2: 사용자 정보 없음");
                    throw new NotFoundException("전화번호로 사용자 정보를 찾을 수 없습니다.");
                }
                System.out.println("Step 2: 사용자 정보 성공적으로 조회 - " + userInfo);
            
                // oid를 MID와 timestamp로 unique하게 구성
                System.out.println("Step 3: oid 생성 시작");
                String timestamp = Long.toString(System.currentTimeMillis());
                String oid = PgParams.MID + "_" + timestamp;
                System.out.println("Step 3: oid 생성 완료 - " + oid);
            
                // 요청전문 구성
                System.out.println("Step 4: 결제 요청 정보 구성 시작");
                ModelAndView payRequest = new ModelAndView();
                payRequest.addObject("price", request.get("price"));
                payRequest.addObject("goodName", request.get("goodName"));
                payRequest.addObject("buyerName", userInfo.get("username"));
                payRequest.addObject("buyerTel", userInfo.get("telno"));
                payRequest.addObject("buyerEmail", userInfo.get("email"));
                payRequest.addObject("returnUrl", Urls.RETURN);
                payRequest.addObject("closeUrl", Urls.CLOSE);
                payRequest.addObject("mid", PgParams.MID);
                payRequest.addObject("signKey", PgParams.SIGN_KEY);
                payRequest.addObject("timestamp", timestamp);
                payRequest.addObject("use_chkfake", PgParams.USE_CHKFAKE);
                payRequest.addObject("oid", oid);
                payRequest.addObject("mKey", PgCommon.sha256(PgParams.SIGN_KEY));
                payRequest.addObject("signature", pgInterfaceCommon.generateSignature(request.get("price"), oid, timestamp));
                payRequest.addObject("verification", pgInterfaceCommon.generateVerification(request.get("price"), oid, timestamp));
                payRequest.setViewName(Views.REQUEST);
                System.out.println("Step 4: 결제 요청 정보 구성 완료");
            
                Map<String, Object> modelMap = payRequest.getModel();
                System.out.println("Step 5: 결제 요청 정보 저장 시작 - " + modelMap);
            
                // 결제 요청 정보를 payments table 에 기록                          
                paymentMapper.savePcRequest(modelMap);
                System.out.println("Step 5: 결제 요청 정보 저장 완료");
            
                // bids table oid 에 기록 
                System.out.println("Step 6: Bids table oid 업데이트 시작");
                Map<String, Object> updateParams = new HashMap<>();
                updateParams.put("telno", request.get("telno"));
                updateParams.put("matchNumber", request.get("matchNumber"));
                updateParams.put("oid", oid);
                int affectedRows = bidMapper.updateBidOid(updateParams);
                System.out.println("Step 6: Bids table 업데이트 완료, affectedRows: " + affectedRows);
            
                // 결제창 호출을 위한 JSP화면 호출        
                System.out.println("Step 7: JSP 호출 시작");
                RequestDispatcher dispatcher = request2.getRequestDispatcher("/WEB-INF/views/" + payRequest.getViewName() + ".jsp");
                dispatcher.forward(request2, response);
                System.out.println("Step 7: JSP 호출 완료");
                return "success";
            
            } catch (Exception e) {
                System.out.println("Error: 시스템 오류 발생 - " + e.getMessage());
                throw new PgException("시스템 오류 : 결제 요청중 오류가 발생하였습니다.", e);
            }
    }
    // 결제 요청
    public ModelAndView pgStart(Map<String, Object> request) {
        try {
            // 전화번호 유효성 검사
            pgInterfaceCommon.validateRequestParameters(request);

            //결제요청에 필요한 사용자 DB정보 요청
            request.put("queryType", "telno");
            request.put("query", request.get("telno"));
            Map<String, Object> userInfo = userService.getUserByQuery(request);
            if (userInfo == null || userInfo.isEmpty()) {
                throw new NotFoundException("전화번호로 사용자 정보를 찾을 수 없습니다.");
            }

            //oid를 MID와 timestamp로 unique하게 구성
            String timestamp = Long.toString(System.currentTimeMillis());
            String oid = PgParams.MID + "_" + timestamp;
            
            // 요청전문 구성
            ModelAndView payRequest = new ModelAndView();
            payRequest.addObject("price", request.get("price"));          //client request정보에서
            payRequest.addObject("goodName", request.get("goodName"));    //client request정보에서 
            payRequest.addObject("buyerName", userInfo.get("username"));  //사용자 DB정보
            payRequest.addObject("buyerTel", userInfo.get("telno"));      //사용자 DB정보
            payRequest.addObject("buyerEmail", userInfo.get("email"));    //사용자 DB정보
            payRequest.addObject("returnUrl", Urls.RETURN);
            payRequest.addObject("closeUrl", Urls.CLOSE);
            payRequest.addObject("mid", PgParams.MID);
            payRequest.addObject("signKey", PgParams.SIGN_KEY);
            payRequest.addObject("timestamp", timestamp);
            payRequest.addObject("use_chkfake", PgParams.USE_CHKFAKE);
            payRequest.addObject("oid", oid);
            payRequest.addObject("mKey", PgCommon.sha256(PgParams.SIGN_KEY));
            payRequest.addObject("signature", pgInterfaceCommon.generateSignature(request.get("price"), oid, timestamp));
            payRequest.addObject("verification", pgInterfaceCommon.generateVerification(request.get("price"), oid, timestamp));
            payRequest.setViewName(Views.REQUEST);
            Map<String, Object> modelMap = payRequest.getModel();

            // 결제 요청 정보를 payments table 에 기록                          
            paymentMapper.savePcRequest(modelMap);
            
            // bids table oid 에 기록 
            Map<String, Object> updateParams = new HashMap<>();
            updateParams.put("telno", request.get("telno"));
            updateParams.put("matchNumber", request.get("matchNumber"));
            updateParams.put("oid", oid);
            int affectedRows = bidMapper.updateBidOid(updateParams);
            if (affectedRows == 0) {
                throw new ZeroAffectedRowException("입찰내용에 거래아이디 세팅오류");
            }
            
            // 결제창 호출을 위한 JSP화면 호출        
            return payRequest;

        } catch (Exception e) {
            throw new PgException("시스템 오류 : 결제 요청중 오류가 발생하였습니다.", e);
        }
        
    }
    
    //결제요청 응답 수신 및 승인 요청
    public ModelAndView pgReturn(String request) {

        // URL 인코딩된 문자열을 Map<String, String>으로 변환
        Map<String, String> params;
        try{
            params = pgInterfaceCommon.parseQueryString(request);      
        } catch (Exception e) {
            throw new UnsupportedEncodingException("Error in parging querysting in pgReturn", e);
        }

        // key-value 형태로 출력 : Debugging용
        // for (Map.Entry<String, String> entry : params.entrySet()) {
        // System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        // }

        // 결제요청 응답의  "resultCode"가 0000일 아닐 경우(오류인 경우) 처리
        if (!"0000".equals(params.get("resultCode"))) {
            logger.error("\n\n------------- pgreturn failed. " + params+"\n");
            
            // ModelAndView 생성 및 데이터 추가
            ModelAndView approveData = new ModelAndView();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                approveData.addObject(entry.getKey(), entry.getValue());
            }
            pgInterfaceCommon.printModelAndView(approveData);
            approveData.setViewName(Views.RETURN); 
            return approveData;
        }
        
        // 결제요청 응답의 "resultCode"가 0000일 경우 처리(요청 성공)
        // 승인 요청 옵션 설정
        String mid = params.get("mid");                     // 상점아이디
        String authToken = params.get("authToken");         // 승인요청 검증 토큰
        String netCancelUrl = params.get("netCancelUrl");   // 망취소요청 URL
        String merchantData = params.get("merchantData");
        long timestamp = System.currentTimeMillis();        // 타임스탬프 [TimeInMillis(Long형)]
        String charset = "UTF-8";                           // 리턴 형식 [UTF-8, EUC-KR]
        String format = "JSON";                             // 리턴 형식 [XML, JSON, NVP]

        // 승인 요청 API URL 설정
        String idc_name = params.get("idc_name");
        String authUrl = params.get("authUrl");

        //== inicis에서 제공한 샘플 코드는 property에서 가져오지만 오류가 발생하여 함수로 변경함 ==  
        String authUrl2 = PgCommon.getAuthUrl(idc_name);

        logger.info("\n\n------------------- pgReturnt authUrl 값: " + authUrl + " pgreturnpost authUrl2 값 : " + authUrl2 + "\n");
        String signature = null;
        String verification = null;
        try {
            // SHA256 해시값 생성 [대상: authToken, timestamp]
            signature = pgInterfaceCommon.createSignature(authToken, timestamp);

            // SHA256 해시값 생성 [대상: authToken, signKey, timestamp]
            verification = pgInterfaceCommon.createVerification(authToken, timestamp);
        } catch (NoSuchAlgorithmException | java.security.NoSuchAlgorithmException e) {
            throw new NoSuchAlgorithmException("Cannot fine SHA-256 algorithm.",e);
        }

        // 결제 승인 요청 옵션 설정
        Map<String, Object> options = new HashMap<>();
        options.put("mid", mid);
        options.put("authToken", authToken);
        options.put("timestamp", timestamp);
        options.put("signature", signature);
        options.put("verification", verification);
        options.put("charset", charset);
        options.put("format", format);
        try{
            //----------------------- 승인 요청 하기
            String urlEncodedOptions = pgInterfaceCommon.convertToUrlEncodedString(options);
            if (!authUrl.equals(authUrl2)) {
                return pgInterfaceCommon.handleNetCancel(netCancelUrl, idc_name, options);
            }

            // 승인요청을 위해 HttpClient 생성 및 POST 요청 전송
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest requestToInicis = HttpRequest.newBuilder()
                    .uri(URI.create(authUrl2))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(urlEncodedOptions.toString()))
                    .build();

            // 응답 받기
            HttpResponse<String> response = client.send(requestToInicis, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
   
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> approveData = objectMapper.readValue(responseBody, Map.class);

            // 승인 결과 데이터베이스에 저장
            paymentMapper.savePCApproval(approveData);

            // Debugging 용    
            // pgInterfaceCommon.printMap(approveData);

            ModelAndView errorView = new ModelAndView("Pc approval results");
            String resultCode = (String) approveData.get("resultCode");

            // 승인결과 코드가 0000이 아닌 경우 오류 페이지로 리다이렉트
            if (!"0000".equals(resultCode)) {
                String errorMsg = (String) approveData.get("resultMsg");
                errorView.addObject("resultCode", resultCode);
                errorView.addObject("errorMessage", errorMsg);
                errorView.setViewName(Views.ERROR); 
                return errorView;
            }

            // 승인 요청 성공 후  회신 데이터를 jsp용 modelAndView에 저장
            ModelAndView modelAndView = new ModelAndView("paymentResultPage"); // 원하는 페이지 이름
            for (Map.Entry<String, Object> entry : approveData.entrySet()) {
                modelAndView.addObject(entry.getKey(), entry.getValue());
            }

            // 입찰결과에 결제 승인 내용 기록
            try {
                Map<String, Object> updateParams = new HashMap<>();
                updateParams.put("oid", approveData.get("MOID"));
                int affectedRows = bidMapper.updateBidPayment(updateParams);
                
            } catch (Exception e) {
                // 예외 처리 로직
                String errorMessageSub = e.getMessage();
                logger.error("\n\n++ 승인은 완료되었으며, 입찰정보에 승인 정보를 갱신하는 중 오류가 발생하였습니다."+ errorMessageSub, e);
                modelAndView.addObject("errorMsg", "승인 되었습니다.  완료 처리 중 오류가 발생하였습니다. 관리자에게 문의하세요.");
                modelAndView.setViewName(Views.ERROR); 
                return modelAndView;
            }

            //jsp view name
            modelAndView.setViewName(Views.RETURN); 
       
            // jsp용 modelAndView pring(debug용)
            // pgInterfaceCommon.printModelAndView(modelAndView);   
            return modelAndView;

        } catch (Exception e) {
            ModelAndView errorAndView = new ModelAndView();
            logger.error("\n\n------------------- Error in pgRetutn " , e);
            errorAndView.addObject("errorMessage", "승인요청중 오류가 발생하였습니다.");
            errorAndView.setViewName(Views.ERROR);
            return errorAndView;
        }
    }   

}
// ---------------------------pgreturn 결제 요청 회신결과
// Key: cp_yn, Value: 
// Key: charset, Value: UTF-8
// Key: orderNumber, Value: INIpayTest_1731929432040
// Key: authToken, Value: +yyiuysaqtzVAwvitOoA8MIvYA9dZREBMwKebgXr3pBShgu/Ny9TkYb+kQBbX192.....................
// Key: resultCode, Value: 0000
// Key: checkAckUrl, Value: https://stgstdpay.inicis.com/api/checkAck
// Key: netCancelUrl, Value: https://stgstdpay.inicis.com/api/netCancel
// Key: mid, Value: INIpayTest
// Key: idc_name, Value: stg
// Key: merchantData, Value:
// Key: resultMsg, Value: 성공
// Key: authUrl, Value: https://stgstdpay.inicis.com/api/payAuth
// Key: cardnum, Value:
// Key: cardUsePoint, Value:
// Key: returnUrl, Value:

// ---------------------------pgreturn 승인 요청 결과입니다.
// Key: CARD_Quota, Value: 00
// Key: CARD_ClEvent, Value:
// Key: CARD_CorpFlag, Value: 9
// Key: buyerTel, Value: 11111111111
// Key: parentEmail, Value:
// Key: applDate, Value: 20241117
// Key: buyerEmail, Value: ihls2oon@naver.com
// Key: OrgPrice, Value:
// Key: p_Sub, Value:
// Key: resultCode, Value: 0000
// Key: mid, Value: INIpayTest
// Key: CARD_UsePoint, Value:
// Key: CARD_Num, Value: *********
// Key: authSignature, Value: 35e157d083a19aa0616f734586a7132275088e102292577dd0b60e2431ff37ff
// Key: tid, Value: StdpayCARDINIpayTest20241117211120693165
// Key: EventCode, Value:
// Key: goodName, Value: 좌석입찰 총 2 건
// Key: TotPrice, Value: 2000
// Key: payMethod, Value: Card
// Key: CARD_MemberNum, Value:
// Key: MOID, Value: INIpayTest_1731845419536
// Key: CARD_Point, Value:
// Key: currency, Value: WON
// Key: CARD_PurchaseCode, Value:
// Key: CARD_PrtcCode, Value: 1
// Key: applTime, Value: 211121
// Key: goodsName, Value: 좌석입찰 총 2 건
// Key: CARD_CheckFlag, Value: 1
// Key: FlgNotiSendChk, Value:
// Key: CARD_Code, Value: 97
// Key: CARD_BankCode, Value: 97
// Key: CARD_TerminalNum, Value:
// Key: P_FN_NM, Value: 카카오머니
// Key: buyerName, Value: 까망이
// Key: p_SubCnt, Value:
// Key: applNum, Value:
// Key: resultMsg, Value: 정상처리되었습니다.
// Key: CARD_Interest, Value: 0
// Key: CARD_SrcCode, Value: O
// Key: CARD_ApplPrice, Value: 2000
// Key: CARD_GWCode, Value: K
// Key: custEmail, Value:
// Key: CARD_Expire, Value:
// Key: CARD_PurchaseName, Value: 카카오머니
// Key: CARD_PRTC_CODE, Value: 1
// Key: payDevice, Value: PC


