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

@Service
public class PgService {
    private static final Logger logger = LoggerFactory.getLogger(BidService.class); 

    @Autowired
    private UserService userService;

    @Autowired
    private PgCommon pgInterfaceCommon;

    @Autowired
    private PaymentMapper paymentMapper;

    // 결제 요청
    public ModelAndView pgStart(Map<String, Object> request) {
        try {
            pgInterfaceCommon.validateRequestParameters(request);  // 전화번호 유효성 검사
            Map<String, Object> userInfo = userService.getUserByTelno(request);

            if (userInfo == null || userInfo.isEmpty()) {
                throw new NotFoundException("사용자 전화번호로 정보를 찾을 수 없습니다.");
            }
        
            // 요청전문 구성
            ModelAndView payRequest = new ModelAndView();
            String timestamp = Long.toString(System.currentTimeMillis());
            String oid = PgParams.MID + "_" + timestamp;
        
            payRequest.addObject("price", request.get("price"));          //from client
            payRequest.addObject("goodName", request.get("goodName"));    //from client
            payRequest.addObject("buyerName", userInfo.get("username"));  //from db query
            payRequest.addObject("buyerTel", userInfo.get("telno"));      //from db query
            payRequest.addObject("buyerEmail", userInfo.get("email"));    //from db query
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
            paymentMapper.savePcRequest(modelMap);       

            return payRequest;

        } catch (Exception e) {
            throw new PgException("pg start중  오류가 발생하였습니다.", e);
        }
        
    }
    //승인 요청
    public ModelAndView pgReturn(String request) {
        Map<String, String> params;
        try{
            params = pgInterfaceCommon.parseQueryString(request);      // URL 인코딩된 문자열을 Map<String, String>으로 변환
        } catch (Exception e) {
            throw new UnsupportedEncodingException("pg리턴의 쿼리 파싱에서 오류가 발생하였습니다.", e);
        }
    
        // 요청에서 "resultCode"가 0000일 아닐 경우 처리
        if (!"0000".equals(params.get("resultCode"))) {
            // 결제 실패 시 처리
            System.out.println("\n\n---------------------------pgreturn pgreturnMobire approval failed. " + params+"\n");

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
        String authUrl2 = PgCommon.getAuthUrl(idc_name);

        System.out.println("\n\npgreturnpost authUrl 값: " + authUrl);
        System.out.println("\n\npgreturnpost authUrl2 값: " + authUrl2);

        String signature = null;
        String verification = null;
        try {
            // SHA256 해시값 생성 [대상: authToken, timestamp]
            signature = pgInterfaceCommon.createSignature(authToken, timestamp);

            // SHA256 해시값 생성 [대상: authToken, signKey, timestamp]
            verification = pgInterfaceCommon.createVerification(authToken, timestamp);
        } catch (NoSuchAlgorithmException | java.security.NoSuchAlgorithmException e) {
            throw new NoSuchAlgorithmException("SHA-256 알고리즘을 찾을 수 없습니다.",e);
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
        logger.info("\n\n--------------------------- pgreturn 승인을 요청합니다. " + authUrl2 + "\n");
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

            // 데이터베이스에 저장
            paymentMapper.savePCApprovel(responseBodyMap);
            pgInterfaceCommon.printMap(responseBodyMap);        //debug용도

            ModelAndView errorView = new ModelAndView("Pc approval results");
            String resultCode = (String) responseBodyMap.get("resultCode");

            // 결과 코드가 0000이 아닌 경우 오류 페이지로 리다이렉트
            if (!"0000".equals(resultCode)) {
                String errorMsg = (String) responseBodyMap.get("resultMsg");  // 메시지를 받아옵니다.
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
            // pgInterfaceCommon.printModelAndView(modelAndView);            // debug 용도 
            modelAndView.setViewName(Views.RETURN); 
            return modelAndView;

        } catch (Exception e) {
            ModelAndView errorAndView = new ModelAndView();
            logger.error("\n\n==========승인요청중 오류가 발생하였습니다", e);
            errorAndView.addObject("errorMessage", "승인요청중 오류가 발생하였습니다.");
            errorAndView.setViewName(Views.ERROR);
            return errorAndView;
        }
    }
    

}


// ---------------------------pgreturn 승인 요청 결과입니다.
// CARD_Quota:00
// CARD_ClEvent:
// CARD_CorpFlag:9
// buyerTel:66667777222
// parentEmail:
// applDate:20241004
// buyerEmail:a@b.com
// OrgPrice:
// p_Sub:
// resultCode:0000
// mid:INIpayTest
// CARD_UsePoint:
// CARD_Num:*********
// authSignature:5063f0c53b20e0d0837fb2a0f3a8e415acc3d4e2a6a20855c53fc44cf436b65b
// tid:StdpayCARDINIpayTest20241004234017326519
// EventCode:
// goodName:좌석입찰 총 3 건
// TotPrice:80003
// payMethod:Card
// CARD_MemberNum:
// MOID:INIpayTest_01234
// CARD_Point:
// currency:WON
// CARD_PurchaseCode:
// CARD_PrtcCode:1
// applTime:234018
// goodsName:좌석입찰 총 3 건
// CARD_CheckFlag:1
// FlgNotiSendChk:
// CARD_Code:97
// CARD_BankCode:97
// CARD_TerminalNum:
// P_FN_NM:카카오머니
// buyerName:난감
// p_SubCnt:
// applNum:
// resultMsg:정상처리되었습니다.
// CARD_Interest:0
// CARD_SrcCode:O
// CARD_ApplPrice:80003
// CARD_GWCode:K
// custEmail:
// CARD_Expire:
// CARD_PurchaseName:카카오머니
// CARD_PRTC_CODE:1
// payDevice:PC


