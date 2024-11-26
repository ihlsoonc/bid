package com.bidsystem.bid.service;

import com.bidsystem.bid.service.ExceptionService.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

@Service
public class PgCommon {
    private static final Logger logger = LoggerFactory.getLogger(PgCommon.class);
    public static final String UrlService = null;

    public static class PgParams {
        public static final String VERSION = "1.0";
        public static final String MID = "INIpayTest"; 
        public static final String CURRENCY = "WON";
        public static final String ACCEPT_METHOD = "HPP(1):va_receipt:below1000:centerCd(Y)";
        public static final String GOPAY_METHOD = "Card:DirectBank:VBank:HPP";
        public static final String SIGN_KEY = "SU5JTElURV9UUklQTEVERVNfS0VZU1RS"; 
        public static final String USE_CHKFAKE = "Y";  
    }

    public static class idc_name {
        public static final String fc = "fc";
        public static final String ks = "ks"; 
        public static final String stg = "stg"; 
    }

    public static class Urls {
        public static final String RETURN = "http://localhost:5000/api/pgreturn";
        public static final String RETURN_MOBILE = "http://localhost:5000/api/pgreturn-mobile";
        public static final String CLOSE = "http://localhost:5000/api/pgclose";
        public static final String REDIRECT = "http://localhost:9000/bidseats";
        public static final String LOGIN = "http://localhost:9000/userlogin";
    }
    
    public static class Views {
        public static final String REQUEST = "INIstdpay_pc_req";
        public static final String RETURN = "INIstdpay_pc_return";
        public static final String REQUEST_MOBILE = "INImobile_mo_req";        // 결제창 시작 전 확인 화면
        public static final String RETURN_MOBILE = "INImobile_mo_return";    // 승인 후 리턴 화면
        public static final String CLOSE = "pgclose";
        public static final String ERROR = "pgerror";
    }

    // 결제 요청 파라미터에서 전화번호가 있는지 확인
    public void validateRequestParameters(Map<String, Object> request) {
        if (!request.containsKey("telno")) {
            throw new BadRequestException("결제요청 파라메터에 전화번호가 필요합니다.");
        }
    }

    // SHA-256 해시 알고리즘을 사용해 서명 생성
    public String generateSignature(Object price, String oid, String timestamp) {
        return sha256("oid=" + oid + "&price=" + price + "&timestamp=" + timestamp);
    }
    // SHA-256 해시 알고리즘을 사용해 검증 서명 생성 
    public String generateVerification(Object price, String oid, String timestamp) {
        return sha256("oid=" + oid + "&price=" + price + "&signKey=" + PgParams.SIGN_KEY + "&timestamp=" + timestamp);
    }
    
    // 결제 요청 시 발생한 예외 처리 및 오류 페이지 반환
    public ModelAndView handleException(Exception e) {
        ModelAndView modelAndView = new ModelAndView(Views.ERROR);
        if (e instanceof NotFoundException) {
            logger.error("++ 결제 사전 요청에서 오류가 발생하였습니다. (사용자 정보 조회 실패)", e);
        } else {
            logger.error("++ 결제 사전 요청에서 오류가 발생하였습니다.", e);
        }
        modelAndView.addObject("errorMessage", "결제 사전 요청에서 오류가 발생하였습니다");
        return modelAndView;
    }
    // Map<String, Object> 데이터를 URL-encoded 쿼리 문자열로 변환
    public String convertToUrlEncodedString(Map<String, Object> data) {
        StringBuilder result = new StringBuilder();
        
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            try {
                if (result.length() > 0) {
                    result.append("&");
                }
                String key = URLEncoder.encode(entry.getKey(), "UTF-8");
                String value = URLEncoder.encode(entry.getValue().toString(), "UTF-8");
                result.append(key).append("=").append(value);
            } catch (java.io.UnsupportedEncodingException e) {
                logger.error("URL 인코딩 중 오류 발생: {}", e.getMessage());
            }
        }
        return result.toString();
    }

    // URL 인코딩된 쿼리 문자열을 Map으로 변환
    public Map<String, String> parseQueryString(String queryString) throws java.io.UnsupportedEncodingException {
        Map<String, String> resultMap = new HashMap<>();
        String[] pairs = queryString.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            String key = URLDecoder.decode(keyValue[0], "UTF-8");
            String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], "UTF-8") : "";
            resultMap.put(key, value);
        }
        return resultMap;
    }

    // 인증 서명 생성
    public String createSignature(String authToken, long timestamp) throws NoSuchAlgorithmException {
        return sha256("authToken=" + authToken + "&timestamp=" + timestamp);
    }

    // 검증 서명 생성
    public String createVerification(String authToken, long timestamp) throws NoSuchAlgorithmException {
        String data = "authToken=" + authToken + "&signKey=" + PgParams.SIGN_KEY + "&timestamp=" + timestamp;
        return sha256(data);
    }

    // 네트워크 취소 요청 처리
    public ModelAndView handleNetCancel(String netCancelUrl, String idcName, Map<String, Object> options) {
        String netCancelUrl2 = getNetCancelUrl(idcName);
        if (netCancelUrl.equals(netCancelUrl2)) {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.postForEntity(netCancelUrl2, options, Map.class);
            logger.info("망취소 요청 결과: {}", response.getBody());
        }

        ModelAndView modelAndView = new ModelAndView(Views.ERROR);
        modelAndView.addObject("errorMessage", "handleNetCancel 하였습니다");
        return modelAndView;
    }

    // SHA-256 해시를 생성
    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(input.getBytes());
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
            throw new RuntimeException("SHA-256 알고리즘을 찾을 수 없습니다", e);
        }
    }

    //  URL 생성
    public static String getAuthUrl(String idcName) {
        String url = "stdpay.inicis.com/api/payAuth";
        switch (idcName) {
            case "fc":
                return "https://fc" + url;
            case "ks":
                return "https://ks" + url;
            case "stg":
                return "https://stg" + url;
            default:
                return "https://default" + url; // 기본 URL을 설정합니다.
        }
    }
    // 네트워크 취소 URL 생성
    public static String getNetCancelUrl(String idcName) {
        String url = "stdpay.inicis.com/api/netCancel";
        switch (idcName) {
            case "fc":
                return "https://fc" + url;
            case "ks":
                return "https://ks" + url;
            case "stg":
                return "https://stg" + url;
            default:
                return "https://default" + url;
        }
}

    // 모바일 결제 요청 URL 생성
    public static String getMobilePayReqUrl(String idcName) {
        String url = "/smart/payReq.ini";
        switch (idcName) {
            case "fc":
                return "https://fcmobile.inicis.com" + url;
            case "ks":
                return "https://ksmobile.inicis.com" + url;
            case "stg":
                return "https://stgmobile.inicis.com" + url;
            default:
                return "https://default.inicis.com" + url;
        }
        }

    // 쿼리 문자열을 JSON 형식으로 변환
    public static String convertQueryStringToJson(String queryString) {
        try {
            // ObjectMapper 인스턴스 생성
            ObjectMapper objectMapper = new ObjectMapper();

            // 쿼리 스트링을 JSON 객체로 변환
            Map<String, String> jsonMap = new HashMap<>();
            String[] pairs = queryString.split("&");

            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                String key = keyValue[0];
                String value = keyValue.length > 1 ? keyValue[1] : ""; // 값이 없을 경우 빈 문자열 사용
                jsonMap.put(key, value);
            }

            // JSON으로 변환
            return objectMapper.writeValueAsString(jsonMap);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ModelAndView 객체 내용을 출력  : 디버깅 용도임
    public static void printModelAndView(ModelAndView modelAndView) {
        System.out.println("View Name: " + modelAndView.getViewName());
        System.out.println("Model Contents:");

        for (Map.Entry<String, Object> entry : modelAndView.getModel().entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
    }

    // Map 내용을 출력 : 디버깅 용도임
    public static void printMap(Map<String, Object> modelMap) {
        if (modelMap == null || modelMap.isEmpty()) {
            System.out.println("Map is empty or null.");
            return;
        }

        for (Entry<String, Object> entry : modelMap.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
    }

    // JSON 문자열을 Map으로 변환
    public static Map<String, String> convertJsonToMap(String jsonString) {
        try {
            // ObjectMapper 인스턴스 생성
            ObjectMapper objectMapper = new ObjectMapper();

            // JSON 문자열을 Map 객체로 변환
            return objectMapper.readValue(jsonString, Map.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
