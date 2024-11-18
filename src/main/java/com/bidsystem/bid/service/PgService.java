package com.bidsystem.bid.service;

import com.bidsystem.bid.service.PgCommon.*;
import com.bidsystem.bid.mapper.PaymentMapper;
import com.bidsystem.bid.service.ExceptionService.*;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    private UserService userService;

    @Autowired
    private PgCommon pgInterfaceCommon;

    @Autowired
    private PaymentMapper paymentMapper;

    private final AtomicInteger counter = new AtomicInteger(1);

    // 결제 요청
    public ModelAndView pgStart(Map<String, Object> request) {
        try {
            // 전화번호 유효성 검사
            pgInterfaceCommon.validateRequestParameters(request);

            //결제요청에 필요한 사용자 DB정보 요청
            request.put("table", "user");
            request.put("queryType", "telno");
            request.put("query", request.get("telno"));
            Map<String, Object> userInfo = userService.getUserByQuery(request);
            if (userInfo == null || userInfo.isEmpty()) {
                throw new NotFoundException("사용자 전화번호로 정보를 찾을 수 없습니다.");
            }

            //oid를 MID와 timestamp로 unique하게 구성
            String timestamp = Long.toString(System.currentTimeMillis());
            String uniqueCounterValue = Integer.toString(counter.getAndIncrement()); // 카운터 값을 증가
            String oid = PgParams.MID + "_" + timestamp+ "_" + uniqueCounterValue;
            
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
            
            // 결제창 호출을 위한 JSP화면 호출        
            return payRequest;

        } catch (Exception e) {
            throw new PgException("Error in pgStart PC.", e);
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
    
        // 요청에서 "resultCode"가 0000일 아닐 경우 처리
        if (!"0000".equals(params.get("resultCode"))) {
            // 결제 실패 시 처리
            System.out.println("\n\n----------------- pgreturn approval failed. " + params+" \n");

            // ModelAndView 생성 및 데이터 추가
            ModelAndView approveData = new ModelAndView();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                approveData.addObject(entry.getKey(), entry.getValue());
            }

            // 반환할 뷰 이름 설정
            approveData.setViewName(Views.RETURN); 
            return approveData;
        }
        
        // 결제요청에서 "resultCode"가 0000일 경우 처리
        // 결제 승인 요청 옵션 설정
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
        logger.info("\n\n------------------- pgreturn 승인을 요청합니다. " + authUrl2 + "\n");
        try{
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
            Map<String, Object> responseBodyMap = objectMapper.readValue(responseBody, Map.class);

            // 승인 결과 데이터베이스에 저장
            paymentMapper.savePCApproval(responseBodyMap);      
            pgInterfaceCommon.printMap(responseBodyMap);        //debug용도///////

            ModelAndView errorView = new ModelAndView("Pc approval results");
            String resultCode = (String) responseBodyMap.get("resultCode");

            // 결과 코드가 0000이 아닌 경우 오류 페이지로 리다이렉트
            if (!"0000".equals(resultCode)) {
                String errorMsg = (String) responseBodyMap.get("resultMsg");
                errorView.addObject("resultCode", resultCode);
                errorView.addObject("errorMessage", errorMsg);
                errorView.setViewName(Views.ERROR); 
                return errorView;
            }
            // 승인 요청 성공 후  회신 데이터 처리
            ModelAndView modelAndView = new ModelAndView("paymentResultPage"); // 원하는 페이지 이름
            for (Map.Entry<String, Object> entry : responseBodyMap.entrySet()) {
                modelAndView.addObject(entry.getKey(), entry.getValue());
            }
            modelAndView.setViewName(Views.RETURN); 
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


