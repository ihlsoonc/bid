package com.bidsystem.bid.controller;

import com.bidsystem.bid.service.ExceptionService.*;
import com.bidsystem.bid.service.UserService;
import com.bidsystem.bid.service.BidService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import jakarta.servlet.http.HttpServletResponse;

import java.net.URI;
import java.net.URLDecoder;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller

@RequestMapping("/api")
public class PgViewController {
    private static final Logger logger = LoggerFactory.getLogger(BidService.class); // 로거 생성
    public class Urls {
        public static final String RETURN = "http://localhost:5000/api/pgreturnpost";  // 서버 프로그램
        public static final String CLOSE = "http://localhost:5000/api/pgclose";        // 서버 프로그램
        public static final String REDIRECT = "http://localhost:8080/bidseats";        // 서버 프로그램
        public static final String LOGIN = "http://localhost:8080/userlogin";        // 서버 프로그램
    }
    
    public class Views {
        public static final String REQUEST = "INIstdpay_pc_req";        // 결제창 시작 전 확인 화면
        public static final String RETURN = "INIstdpay_pc_return";    // 승인 후 리턴 화면
        public static final String CLOSE = "close";         // 결제 종료 화면
        public static final String ERROR = "error";         // 결제 종료 화면
    }
    public class PgParams {
        public static final String VERSION = "1.0";
        public static final String MID = "INIpayTest"; 
        public static final String CURRENCY = "WON";
        public static final String ACCEPT_METHOD = "HPP(1):va_receipt:below1000:centerCd(Y)";
        public static final String GOPAY_METHOD = "Card:DirectBank:VBank:HPP";
        public static final String OID = "INIpayTest_01234"; 
        public static final String SIGN_KEY = "SU5JTElURV9UUklQTEVERVNfS0VZU1RS"; 
        public static final String USE_CHKFAKE = "Y";  
    }

    @Autowired
    private BidService bidService;
    @Autowired
    private UserService userService;

    @GetMapping("/pgstart")
    public ModelAndView paymentPage(@RequestParam Map<String, Object> request, Model model, HttpServletResponse response) {

        try {
            if (!request.containsKey("telno")) {
                throw new BadRequestException("결제요청 파라메터에 전화번호가 필요합니다.");
            }

            //요청 전문에 전화번호, 이메일을 추가하기 위해 사용자 정보 조회
            Map<String, Object> results = userService.getUserByTelno(request);
    
            // 결과가 비어있을 경우 NotFoundException 예외 발생
            if (results == null || results.isEmpty()) {
                throw new NotFoundException("사용자 전화번호로 정보를 찾을 수 없습니다.");
            }

            // 승인사전 요청을 위한 데이터 구성 
            ModelAndView modelAndView = new ModelAndView();
            String timestamp = Long.toString(System.currentTimeMillis());
            modelAndView.addObject("price", request.get("price"));
            modelAndView.addObject("goodname", request.get("goodName"));
            modelAndView.addObject("buyername", results.get("username"));
            modelAndView.addObject("buyertel", results.get("telno"));
            modelAndView.addObject("buyeremail", results.get("email"));
            modelAndView.addObject("returnUrl", Urls.RETURN);
            modelAndView.addObject("closeUrl", Urls.CLOSE);
            modelAndView.addObject("mid", PgParams.MID);
            modelAndView.addObject("signKey", PgParams.SIGN_KEY);
            modelAndView.addObject("timestamp", timestamp);
            modelAndView.addObject("use_chkfake", PgParams.USE_CHKFAKE);
            modelAndView.addObject("oid", PgParams.OID);
            modelAndView.addObject("returnUrl", Urls.RETURN);
            modelAndView.addObject("closeUrl", Urls.CLOSE);
            String orderNumber = PgParams.MID + "_" + timestamp;
            modelAndView.addObject("orderNumber", orderNumber);
            String mKey = sha256(PgParams.SIGN_KEY);
            Object price = request.get("price");
            String signature = sha256("oid=" + PgParams.OID + "&price=" + price + "&timestamp=" + timestamp);
            String verification = sha256("oid=" + PgParams.OID + "&price=" + price + "&signKey=" + PgParams.SIGN_KEY + "&timestamp=" + timestamp);
            modelAndView.addObject("mKey", mKey);
            modelAndView.addObject("signature", signature);
            modelAndView.addObject("verification", verification);
            modelAndView.setViewName(Views.REQUEST);
            return modelAndView;
    
        } catch (Exception e) {
            ModelAndView modelAndView = new ModelAndView();
            if (e instanceof NotFoundException) {
                logger.error("\n\n++ 결제 사전 요청에서 오류가 발생하였습니다. (사용자 정보 조회 실패)", e);
            } else {
                logger.error("\n\n++ 결제 사전 요청에서 오류가 발생하였습니다.", e);
            }
            modelAndView.addObject("errorMessage", "결제 사전 요청에서 오류가 발생하였습니다");
            modelAndView.setViewName(Views.ERROR);
            return modelAndView;
        }
    }

    @PostMapping("/pgreturnpost")
    public ModelAndView pgReturnPost(@RequestBody String request, Model model) {
        Map<String, String> params;
                    // URL 인코딩된 문자열을 Map<String, String>으로 변환
        try {

            params = parseQueryString(request);

        } catch (Exception e) {
            throw new UnsupportedEncodingException("pg리턴의 쿼리 파싱에서 오류가 발생하였습니다.", e);
        }
    
        // 요청에서 "resultCode"가 0000일 아닐 경우 처리
        if (!"0000".equals(params.get("resultCode"))) {
            // 결제 실패 시 처리
            logger.error("\n\n---------------------------pgreturn 승인요청이 실패하였습니다. " + "\n");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
            }
            Map<String, Object> approveData = new HashMap<>();
            approveData.put("resultCode", params.get("resultCode"));
            approveData.put("resultMsg", params.get("resultMsg"));
            approveData.put("tid", params.get("tid"));
            approveData.put("MOID", params.get("MOID"));
            approveData.put("TotPrice", params.get("TotPrice"));
            approveData.put("goodName", params.get("goodName"));
            approveData.put("applDate", params.get("applDate"));
            approveData.put("applTime", params.get("applTime"));
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("resultMap", approveData);
            modelAndView.setViewName(Views.RETURN); 
            return modelAndView;
        }
        // 결제요청에서 "resultCode"가 0000일 경우 처리
        // 1. 전문 필드 값 설정
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
        String authUrl2 = UrlService.getAuthUrl(idc_name);

        System.out.println("\n\npgreturnpost authUrl 값: " + authUrl);
        System.out.println("\n\npgreturnpost authUrl2 값: " + authUrl2);

        String signature = null;
        String verification = null;
        try {
            // SHA256 해시값 생성 [대상: authToken, timestamp]
            signature = createSignature(authToken, timestamp);

            // SHA256 해시값 생성 [대상: authToken, signKey, timestamp]
            verification = createVerification(authToken, timestamp);
        } catch (NoSuchAlgorithmException e) {
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
        logger.info("\n\n---------------------------pgreturn 승인을 요청합니다. " + authUrl2 + "\n");
        try{
            String urlEncodedOptions = convertToUrlEncodedString(options);
            if (!authUrl.equals(authUrl2)) {
                return handleNetCancel(netCancelUrl, idc_name, options);
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

            // 응답 본문 출력 (Map 형태로 반환됨)
            if (responseBodyMap != null) {
                System.out.println("\n\n---------------------------pgreturn 승인 요청 결과입니다. ");
                for (Map.Entry<String, Object> entry : responseBodyMap.entrySet()) {
                    System.out.println(entry.getKey()+ ":" + entry.getValue());
                }
            } else {
                System.out.println("\n\n---------------------------pgreturn 승인 요청 결과가 null입니다.");
            };

            ModelAndView modelAndView = new ModelAndView();
            String resultCode = (String) responseBodyMap.get("resultCode");

            // 결과 코드가 0000이 아닌 경우 오류 페이지로 리다이렉트
            if (!"0000".equals(resultCode)) {
                String errorMsg = (String) responseBodyMap.get("resultMsg");  // 메시지를 받아옵니다.
                modelAndView.addObject("resultCode", resultCode);
                modelAndView.addObject("errorMessage", errorMsg);
                modelAndView.setViewName(Views.ERROR); 
                return modelAndView;
            }
            
            // 결제 요청 성공 후  회신 데이터 처리
            Map<String, Object> approveData = new HashMap<>();
            approveData.put("resultCode", responseBodyMap.get("resultCode"));
            approveData.put("resultMsg", responseBodyMap.get("resultMsg"));
            approveData.put("tid", responseBodyMap.get("tid"));
            approveData.put("MOID", responseBodyMap.get("MOID"));
            approveData.put("TotPrice", responseBodyMap.get("TotPrice"));
            approveData.put("goodName", responseBodyMap.get("goodName"));
            approveData.put("payMethod", responseBodyMap.get("P_FN_NM"));
            approveData.put("buyerName", responseBodyMap.get("buyerName"));
            approveData.put("applDate", responseBodyMap.get("applDate"));
            approveData.put("applTime", responseBodyMap.get("applTime"));
            approveData.put("buyerTel", responseBodyMap.get("buyerTel"));

            // 입찰결과에 결제 승인 내용 기록
            try {

                //입찰 정보 업데이트 파라메터 구성
                Map<String, Object> updateParams = new HashMap<>();
                updateParams.put("telno", approveData.get("buyerTel"));
                updateParams.put("bidAmount", approveData.get("TotPrice"));
                updateParams.put("tid", approveData.get("tid"));
                updateParams.put("payMethod", approveData.get("payMethod"));

                // 입찰 정보 업데이트 호출
                Map<String, Object> bidResults = bidService.updateBidPayment(updateParams);

                //승인결과 화면을 render
                Object bidMsg = bidResults.get("message");
                approveData.put("bidUpdateResults", bidMsg);
                System.out.println("\n\n====================approveData==========================\n\n");
                System.out.println(approveData);

                modelAndView.addObject("resultMap", approveData);
                modelAndView.setViewName(Views.RETURN); 
                return modelAndView;
                
            } catch (Exception e) {
                    // 예외 처리 로직
                    String errorMessageSub = e.getMessage();
                    logger.error("\n\n++ 승인은 완료되었으며, 입찰정보에 승인 정보를 갱신하는 중 오류가 발생하였습니다."+ errorMessageSub, e);
                    modelAndView.addObject("errorMessage", "승인 완료 후 사용자 정보 갱신 중 오류가 발생하였습니다");
                    modelAndView.setViewName(Views.ERROR); 
                    return modelAndView;
            }
        } catch (Exception e) {
            ModelAndView modelAndView = new ModelAndView();
            logger.error("\n\n==========승인요청중 오류가 발생하였습니다", e);
            modelAndView.addObject("errorMessage", "승인요청중 오류가 발생하였습니다.");
            modelAndView.setViewName(Views.ERROR);
            return modelAndView;
        }
    }

    @GetMapping("/pgclose")
    // public ModelAndView pgClose(@RequestParam Map<String, Object> request, Model model) {
    //     logger.info("\n\n---------------------------Pgclose가 호출되었습니다\n\n");
    //     ModelAndView modelAndView = new ModelAndView();
    //     modelAndView.addObject("errorMessage", "pg close가 호출되었습니다.");
    //     modelAndView.setViewName(Views.CLOSE); 
    //     return modelAndView;
    // }


    // @GetMapping("/pgstart")
    public ModelAndView paymentPage2(@RequestParam Map<String, Object> request, Model model, HttpServletResponse response) {

        try {
            if (!request.containsKey("telno")) {
                throw new BadRequestException("결제요청 파라메터에 전화번호가 필요합니다.");
            }

            //요청 전문에 전화번호, 이메일을 추가하기 위해 사용자 정보 조회
            Map<String, Object> results = userService.getUserByTelno(request);
    
            // 결과가 비어있을 경우 NotFoundException 예외 발생
            if (results == null || results.isEmpty()) {
                throw new NotFoundException("사용자 전화번호로 정보를 찾을 수 없습니다.");
            }

            // 승인사전 요청을 위한 데이터 구성 
            ModelAndView modelAndView = new ModelAndView();
            String timestamp = Long.toString(System.currentTimeMillis());
            modelAndView.addObject("price", request.get("price"));
            modelAndView.addObject("goodname", request.get("goodName"));
            modelAndView.addObject("buyername", results.get("username"));
            modelAndView.addObject("buyertel", results.get("telno"));
            modelAndView.addObject("buyeremail", results.get("email"));
            modelAndView.addObject("returnUrl", Urls.RETURN);
            modelAndView.addObject("closeUrl", Urls.CLOSE);
            modelAndView.addObject("mid", PgParams.MID);
            modelAndView.addObject("signKey", PgParams.SIGN_KEY);
            modelAndView.addObject("timestamp", timestamp);
            modelAndView.addObject("use_chkfake", PgParams.USE_CHKFAKE);
            modelAndView.addObject("oid", PgParams.OID);
            modelAndView.addObject("returnUrl", Urls.RETURN);
            modelAndView.addObject("closeUrl", Urls.CLOSE);
            String orderNumber = PgParams.MID + "_" + timestamp;
            modelAndView.addObject("orderNumber", orderNumber);
            String mKey = sha256(PgParams.SIGN_KEY);
            Object price = request.get("price");
            String signature = sha256("oid=" + PgParams.OID + "&price=" + price + "&timestamp=" + timestamp);
            String verification = sha256("oid=" + PgParams.OID + "&price=" + price + "&signKey=" + PgParams.SIGN_KEY + "&timestamp=" + timestamp);
            modelAndView.addObject("mKey", mKey);
            modelAndView.addObject("signature", signature);
            modelAndView.addObject("verification", verification);
            modelAndView.setViewName(Views.REQUEST);
            return modelAndView;
    
        } catch (Exception e) {
            ModelAndView modelAndView = new ModelAndView();
            if (e instanceof NotFoundException) {
                logger.error("\n\n++ 결제 사전 요청에서 오류가 발생하였습니다. (사용자 정보 조회 실패)", e);
            } else {
                logger.error("\n\n++ 결제 사전 요청에서 오류가 발생하였습니다.", e);
            }
            modelAndView.addObject("errorMessage", "결제 사전 요청에서 오류가 발생하였습니다");
            modelAndView.setViewName(Views.ERROR);
            return modelAndView;
        }
    }
    // Map<String, Object>을 URL-encoded 형식으로 변환하는 메서드
    public static String convertToUrlEncodedString(Map<String, Object> data) {
        StringBuilder result = new StringBuilder();
        
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            try {
                if (result.length() > 0) {
                    result.append("&");
                }
                String key = URLEncoder.encode(entry.getKey(), "UTF-8");
                String value = URLEncoder.encode(entry.getValue().toString(), "UTF-8");
                result.append(key).append("=").append(value);
            } catch (UnsupportedEncodingException e) {
                // 예외 발생 시 처리
                System.err.println("URL 인코딩 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
            } catch (java.io.UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result.toString();
        }

    // URL 인코딩된 쿼리 문자열을 Map으로 변환하는 메서드
    private Map<String, String> parseQueryString(String queryString) throws java.io.UnsupportedEncodingException {
        Map<String, String> resultMap = new HashMap<>();
        String[] pairs = queryString.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            String key = null;
            String value = null;
            
            try {
                key = URLDecoder.decode(keyValue[0], "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // RuntimeException으로 감싸서 던지되, 원인 예외(e)를 함께 전달
                throw new RuntimeException("pgreturn 쿼리 처리 중 오류가 발생하였습니다.", e);
            }
            try {
                value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], "UTF-8") : "";
            } catch (UnsupportedEncodingException e) {
                // RuntimeException으로 감싸서 던지되, 원인 예외(e)를 함께 전달
                throw new RuntimeException("pgreturn 쿼리 처리 중 오류가 발생하였습니다.", e);
            }
            resultMap.put(key, value);
        }

        return resultMap;
    }

    private String createSignature(String authToken, long timestamp) throws NoSuchAlgorithmException {
        String data = "authToken=" + authToken + "&timestamp=" + timestamp;
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes());
            StringBuilder hexString = new StringBuilder();
    
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new NoSuchAlgorithmException(null,e);
        }   
    }

    private String createVerification(String authToken, long timestamp) throws NoSuchAlgorithmException {
        String signKey = PgParams.SIGN_KEY;  // pgParams.signKey에 해당하는 값
        String data = "authToken=" + authToken + "&signKey=" + signKey + "&timestamp=" + timestamp;
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new NoSuchAlgorithmException(null,e);
        }
        byte[] hash = digest.digest(data.getBytes());
        StringBuilder hexString = new StringBuilder();

        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }

        return hexString.toString();
    }
    
    private ModelAndView handleNetCancel(String netCancelUrl, String idcName, Map<String, Object> options) {
        String netCancelUrl2 = UrlService.getNetCancelUrl(idcName);
        if (netCancelUrl.equals(netCancelUrl2)) {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.postForEntity(netCancelUrl2, options, Map.class);

            System.out.println("망취소 요청 결과: " + response.getBody());
        }

        String errorMsg = "handleNetCancel 하였습니다";
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("errorMessage", errorMsg);
        modelAndView.setViewName(Views.ERROR); 
        return modelAndView;
    }

    public static String sha256(String input) {
        try {

            MessageDigest digest = null;
            try {
                digest = MessageDigest.getInstance("SHA-256");
            } catch (java.security.NoSuchAlgorithmException e) {
                throw new NoSuchAlgorithmException(null,e);
            }

            // 입력 데이터를 바이트 배열로 변환
            byte[] encodedHash = digest.digest(input.getBytes());

            // 바이트 배열을 16진수 문자열로 변환하여 반환
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    public class UrlService {
        public static String getAuthUrl(String idcName) {
            String url = "stdpay.inicis.com/api/payAuth";
            String authUrl = null;
    
            switch (idcName) {
                case "fc":
                    authUrl = "https://fc" + url;
                    break;
                case "ks":
                    authUrl = "https://ks" + url;
                    break;
                case "stg":
                    authUrl = "https://stg" + url;
                    break;
                default:
                    // 기본값이 필요하면 설정 가능
                    break;
            }
    
            return authUrl;
        }
    
        public static String getNetCancelUrl(String idcName) {
            String url = "stdpay.inicis.com/api/netCancel";
            String netCancelUrl = null;
    
            switch (idcName) {
                case "fc":
                    netCancelUrl = "https://fc" + url;
                    break;
                case "ks":
                    netCancelUrl = "https://ks" + url;
                    break;
                case "stg":
                    netCancelUrl = "https://stg" + url;
                    break;
                default:
                    // 기본값이 필요하면 설정 가능
                    break;
            }
    
            return netCancelUrl;
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


