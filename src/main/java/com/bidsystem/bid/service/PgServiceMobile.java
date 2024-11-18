package com.bidsystem.bid.service;

import com.bidsystem.bid.service.PgCommon.*;
import com.bidsystem.bid.mapper.PaymentMapper;
import com.bidsystem.bid.service.ExceptionService.*;

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
public class PgServiceMobile {
    private static final Logger logger = LoggerFactory.getLogger(BidService.class); 

    @Autowired
    private UserService userService;

    @Autowired
    private PgCommon pgInterfaceCommon;

    @Autowired
    private PaymentMapper paymentMapper;

    //결제 요청
    public ModelAndView pgStartMobile(Map<String, Object> request) {
        try {
            // 전화번호 유효성 검사
            pgInterfaceCommon.validateRequestParameters(request); 

            //결제요청에 필요한 사용자 DB정보 요청
            request.put("table", "user");
            request.put("queryType", "telno");
            request.put("query", request.get("telno"));
            Map<String, Object> userInfo = userService.getUserByQuery(request);
            if (userInfo == null || userInfo.isEmpty()) {
                throw new NotFoundException("User query by telno failed in pgStartMobile");
            }

            //oid를 MID와 timestamp로 unique하게 구성
            String timestamp = Long.toString(System.currentTimeMillis());
            String oid = PgParams.MID + "_" + timestamp;
    
            // 요청 전문 구성
            ModelAndView payRequest = new ModelAndView();
            payRequest.addObject("P_INI_PAYMENT", "CARD");       
            payRequest.addObject("P_AMT", request.get("price"));          // client request정보에서
            payRequest.addObject("P_GOODS", request.get("goodName"));    // client request정보에서
            
            payRequest.addObject("P_UNAME", userInfo.get("username"));  // 사용자 DB정보
            payRequest.addObject("P_MOBILE", userInfo.get("telno"));    // 사용자 DB정보
            payRequest.addObject("P_EMAIL", userInfo.get("email"));    // 사용자 DB정보
            payRequest.addObject("P_NEXT_URL", Urls.RETURN_MOBILE);
            payRequest.addObject("P_NOTI_URL", "");   // 가상계좌입금통보 URL 가상계좌 결제 시 필수
            payRequest.addObject("P_HPP_METHOD", "1"); // 휴대폰결제 상품유형 [1:컨텐츠, 2:실물]
            payRequest.addObject("P_MID", PgParams.MID);
            payRequest.addObject("P_OID", oid); 
            payRequest.setViewName(Views.REQUEST_MOBILE);
            Map<String, Object> modelMap = payRequest.getModel();

            // 결제 요청 정보를 payments table 에 기록     
            paymentMapper.saveMobileRequest(modelMap);

            // 결제창 호출을 위한 JSP화면 호출        
            return payRequest;

        } catch (Exception e) {
            throw new PgException("Error in pgStart Mobile.", e);
        }
        
    }

    //결제요청 응답 수신 및 승인 요청
    public ModelAndView pgReturnMobile(String request) {

        // URL 인코딩된 문자열을 Map<String, String>으로 변환
        Map<String, String> params;
        try{
            params = pgInterfaceCommon.parseQueryString(request);      
        } catch (Exception e) {
            throw new UnsupportedEncodingException("Error in parging querysting in pgReturn Mobile", e);
        }
        
        String P_TID = params.get("P_TID");         // 승인요청 검증 토큰
        String idc_name = params.get("idc_name");    
        String P_RMESG1 = params.get("P_RMESG1");    //debug

        //== inicis에서 제공한 샘플 코드는 아래와 같이 property에서 가져오지만 오류가 발생하여 함수로 변경함 ==  
        // String expectedUrl = ResourceBundle.getBundle("properties/idc_name.mobile").getString(idc_name);
        String expectedUrl = pgInterfaceCommon.getMobilePayReqUrl(idc_name);

        String P_REQ_URL = params.get("P_REQ_URL");
        String P_STATUS = params.get("P_STATUS");

        // 결제요청에서 "P_STATUS"가 00일 아닐 경우 처리
        if (!"00".equals(P_STATUS) || !P_REQ_URL.equals(expectedUrl)) {            // 결제요청 실패 시 처리
            System.out.println("\n\n---------------------------pgreturnMobire approval failed. " + params+"\n");
            
            // ModelAndView 생성 및 데이터 추가
            ModelAndView approveData = new ModelAndView();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                approveData.addObject(entry.getKey(), entry.getValue());
            }

            approveData.setViewName(Views.RETURN_MOBILE); 
            return approveData;
        }
        
        // 결제요청에서 "P_STATUS"가 00일 경우 처리
        // 결제 승인 요청 옵션 설정
        Map<String, Object> options = new HashMap<>();
        String P_MID = PgParams.MID;       
        if (P_TID.length() >= 20) {
            try {
                P_MID = P_TID.substring(10, 20);
                System.out.println("P_MID: " + P_MID);
            } catch (StringIndexOutOfBoundsException e) {
                System.out.println("문자열의 길이가 충분하지 않습니다: " + e.getMessage());
            }
        } else {
            System.out.println("P_TID의 길이가 충분하지 않습니다.");
        }
        P_REQ_URL = P_REQ_URL + "?P_TID=" + P_TID + "&P_MID=" + P_MID;
        options.put("P_MID", P_MID);
        options.put("P_TID", P_TID);
        try{
            String urlEncodedOptions = pgInterfaceCommon.convertToUrlEncodedString(options);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest requestToInicis = HttpRequest.newBuilder()
                    .uri(URI.create(P_REQ_URL))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(urlEncodedOptions.toString()))
                    .build();

            // 응답 받기
            HttpResponse<String> response = client.send(requestToInicis, HttpResponse.BodyHandlers.ofString());
            String jsonString = pgInterfaceCommon.convertQueryStringToJson(response.body());

            // JSON 문자열을 Map으로 변환
            Map<String, String> paymentResult = pgInterfaceCommon.convertJsonToMap(jsonString);

            // Map<String, String>을 Map<String, Object>로 변환하여 데이터베이스에 저장
            Map<String, Object> modelMap = new HashMap<>(paymentResult);

            // 승인 결과 데이터베이스에 저장
            paymentMapper.saveMobileApproval(modelMap);
            pgInterfaceCommon.printMap(modelMap);        //debug용도///////

            ModelAndView errorView = new ModelAndView();
            P_STATUS = (String) paymentResult.get("P_STATUS");

            // 승인결과 코드가 00이 아닌 경우 오류 페이지로 리다이렉트
            if (!"00".equals(P_STATUS)) {
                errorView.addObject("resultCode", P_STATUS);
                errorView.addObject("errorMessage", P_RMESG1);
                errorView.setViewName(Views.ERROR); 
                return errorView;
            }
            
            // 승인 요청 성공 후  회신 데이터 처리
            ModelAndView modelAndView = new ModelAndView("paymentResultPage"); // 원하는 페이지 이름
            for (Map.Entry<String, String> entry : paymentResult.entrySet()) {
                modelAndView.addObject(entry.getKey(), entry.getValue());
            }
            // printModelAndView(modelAndView);            //debug용도///////
            modelAndView.setViewName(Views.RETURN_MOBILE); 
            return modelAndView;

        } catch (Exception e) {
            ModelAndView errorAndView = new ModelAndView();
            logger.error("\n\n------------------- Error in pgRetutn " , e);
            errorAndView.addObject("errorMessage", "Error occured in approval request.");
            errorAndView.setViewName(Views.ERROR);
            return errorAndView;
        }
    }

}
// ---------------------------pgstart results :
// P_STATUS: 00
// P_RMESG1: 정상 처리 되었습니다.
// P_TID: INIMX_AUTHINIpayTest20241029164022745656
// P_REQ_URL: https://ksmobile.inicis.com/smart/payReq.ini
// P_NOTI: 
// idc_name: ks
// P_AMT: 2000

// ---------------------------pgreturn approval results :
// CARD_CorpFlag:0
// P_NOTEURL:
// P_CARD_ISSUER_CODE:00
// EventCode:
// P_UNAME:정상상
// P_CARD_NUM:529942*********0
// P_MERCHANT_RESERVED:dXNlcG9pbnQ9MCY%3D
// P_TID:INIMX_CARDINIpayTest20241030120339525932
// P_CARD_APPLPRICE:2000
// P_CARD_INTEREST:0
// P_MNAME:
// P_RMESG2:00
// P_RMESG1:성공적으로 처리 하였습니다.
// P_NOTI:
// P_CARD_MEMBER_NUM:
// P_OID:6e9143d98da24514
// P_FN_NM:현대카드
// P_AMT:2000
// P_MID:INIpayTest
// P_NEXT_URL:http://localhost:5000/api/pgreturn-mobile
// P_CARD_PURCHASE_NAME:현대카드
// P_TYPE:CARD
// P_CARD_PRTC_CODE:1
// P_AUTH_DT:20241030120339
// P_CARD_ISSUER_NAME:현대(다이너스)카드
// P_STATUS:00
// P_CARD_CHECKFLAG:0
// P_FN_CD1:04
// P_CARD_PURCHASE_CODE:04
// P_AUTH_NO:00694683
