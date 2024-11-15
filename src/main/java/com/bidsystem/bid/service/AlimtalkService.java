package com.bidsystem.bid.service;
import com.bidsystem.bid.mapper.MatchMapper;
import com.bidsystem.bid.mapper.BidMapper;
import com.bidsystem.bid.service.ExceptionService.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.tomcat.util.http.parser.Authorization;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AlimtalkService {
    @Autowired
    private MatchMapper matchMapper;
    @Autowired
    private BidMapper bidMapper;

    // RestTemplate 빈을 생성합니다
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BIZTALK_TEST = "http://www.biztalk-center.co.kr/biztalkapi_test/";
    private static final String BIZTALK_RUN = "http://www.biztalk-center.co.kr/biztalkapi/";

    //EXCEL에 있는 정보
    private static final String CLIENT_ID = "wisam"; 
    private static final String CLIENT_SECRET = "66fd8d80344a5bb4b28f85ee02cd378178bacf8e";
    
    //SAMPLE코드에 있는 정보
    public static class ALIMTALK {
        public static final String TOKEN_URL = "https://www.biztalk-api.com/v2/auth/getToken";
        public static final String SEND_URL = "https://www.biztalk-api.com/v2/kko/sendAlimTalk"; 
        public static final String RESULT_URL = "https://www.biztalk-api.com/v2/kko/getResultAll"; 
    }
    private static final String BSID = "wisam";
    private static final String PASSWD = "b14c2d414288409ff3948159e1b9306c7c48a302";
    private static final String SENDER_KEY = "4946de9fe9945a158bf5b9a47c8bbc7e6d3ceeb5";
    private static final String TEMPLATE_CODE = "bidawardnotice";
    private static final String COUNTRY_CODE = "82";

    @SuppressWarnings("deprecation")
    public String getAccessToken() {
        System.out.println("getAccessToken 메서드 시작");
    
        String retToken = "";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("bsid", BSID);
        jsonObject.put("passwd", PASSWD);
    
        try {
            // RestTemplate 설정
            RestTemplate restTemplate = new RestTemplate();
    
            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    
            // 요청 생성
            HttpEntity<String> entity = new HttpEntity<>(jsonObject.toString(), headers);
            System.out.println("HttpEntity 생성 완료: " + entity);
    
            // POST 요청 수행
            System.out.println("POST 요청 전송 중...");
            ResponseEntity<String> response = restTemplate.exchange(ALIMTALK.TOKEN_URL, HttpMethod.POST, entity, String.class);
    
            // 응답 상태 코드 확인
            if (response.getStatusCodeValue() != 200) {
                System.out.println("getToken 응답 오류: HTTP " + response.getStatusCodeValue());
                return "";
            } else {
                System.out.println("응답 상태 코드 확인 완료: 200 OK");
            }
    
            // JSON 응답 파싱
            ObjectMapper objectMapper = new ObjectMapper();
    
            // JSON 문자열을 JsonNode로 파싱
            JsonNode responseJson = objectMapper.readTree(response.getBody());
            System.out.println("response 파싱된 JSON 객체: " + responseJson);
    
            // 응답 코드 확인
            String responseCode = responseJson.get("responseCode").asText();
            System.out.println("응답 코드(responseCode): " + responseCode);
    
            // 응답 코드가 1000인지 확인
            if ("1000".equals(responseCode)) {
                retToken = responseJson.get("token").asText();
                System.out.println("토큰 발급 성공: " + retToken);
            } else {
                String errorMsg = responseJson.get("msg").asText();
                System.out.println("토큰 발급 실패: " + responseCode + " - " + errorMsg);
                return "";
            }
        } catch (Exception e) {
            throw new ServerException("Error occured in getAccess Token for Alimtalk", e);
        }
        System.out.println("getAccessToken 메서드 종료, 반환할 토큰: " + retToken);
        return retToken;
    }

    public void sendOneAlimTalk(String alimMessage, String telno, String token) {   
        System.out.println("sendAlimTalk 메서드 시작");
    
        Map<String, Object> requestBody = new HashMap<>();
        System.out.println("알림톡 메시지 생성: " + alimMessage);
    
        String msgIdx = String.valueOf(System.currentTimeMillis());
        System.out.println("메시지 인덱스(msgIdx) 생성: " + msgIdx);
    
        // 요청 본문 구성
        requestBody.put("msgIdx", msgIdx);
        requestBody.put("countryCode", COUNTRY_CODE);
        requestBody.put("recipient", telno);
        requestBody.put("senderKey", SENDER_KEY);
        requestBody.put("message", alimMessage);
        requestBody.put("tmpltCode", TEMPLATE_CODE);
        requestBody.put("resMethod", "PUSH");
        System.out.println("요청 본문(requestBody) 구성 완료: " + requestBody);
    
        // HTTP 헤더 구성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("bt-token", token);
        System.out.println("HTTP 헤더(headers) 구성 완료: " + headers);
    
        // HttpEntity에 요청 본문과 헤더 설정
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        System.out.println("HttpEntity 생성 완료: " + entity);
    
        // 알림톡 전송 요청
        System.out.println("알림톡 전송 요청 시작");
        ResponseEntity<String> response = restTemplate.exchange(ALIMTALK.SEND_URL, HttpMethod.POST, entity, String.class);
    
        // 응답 상태 확인
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("알림톡 전송 성공: " + response.getBody());
        } else {
            System.out.println("알림톡 전송 오류: HTTP " + response.getStatusCode());
        }
    
        System.out.println("sendAlimTalk 메서드 종료");
    }
    

    // // 3. 알림톡 결과 조회 메서드
    // public Map<String, Object> getAlimResult(String[] msgIdxArr, String token) throws IOException {
    //     HttpHeaders headers = new HttpHeaders();
    //     headers.setContentType(MediaType.APPLICATION_JSON);
    //     headers.set("bt-token", token);

    //     HttpEntity<String> entity = new HttpEntity<>(headers);
    //     ResponseEntity<String> response = restTemplate.exchange(ALIMTALK.RESULT_URL, HttpMethod.GET, entity, String.class);

    //     Map<String, Object> resultMap = new HashMap<>();
    //     int successCount = 0;
    //     int failCount = 0;
    //     List<Map<String, String>> failDetails = new ArrayList<>();

    //     if (response.getStatusCode() == HttpStatus.OK) {
    //         JsonNode jsonNode = objectMapper.readTree(response.getBody());
    //         if ("1000".equals(jsonNode.path("responseCode").asText())) {
    //             JsonNode responseArray = jsonNode.path("response");
    //             for (JsonNode item : responseArray) {
    //                 String msgIdx = item.path("msgIdx").asText();
    //                 String resultCode = item.path("resultCode").asText();

    //                 boolean isSuccess = Arrays.asList(msgIdxArr).contains(msgIdx) && "1000".equals(resultCode);
    //                 if (isSuccess) {
    //                     successCount++;
    //                 } else {
    //                     failCount++;
    //                     Map<String, String> failDetail = new HashMap<>();
    //                     failDetail.put("msgIdx", msgIdx);
    //                     failDetail.put("reason", item.path("reason").asText());
    //                     failDetails.add(failDetail);
    //                 }
    //             }
    //             resultMap.put("successCount", successCount);
    //             resultMap.put("failCount", failCount);
    //             resultMap.put("failDetails", failDetails);
    //         } else {
    //             throw new RuntimeException("결과 조회 실패: " + jsonNode.path("msg").asText());
    //         }
    //     }
    //     return resultMap;
    // }

public Map<String, Object> sendAlimtalkByMatch(String matchNumber, String ACCESS_TOKEN) throws IOException {
    // =================== 테스트용 데이터
    String telno = "01092355073";
    ACCESS_TOKEN = "111111111111111111111";
    //========================================


    // 경기 정보 구성
    Map<String, Object> matchParams = new HashMap<>();
    matchParams.put("matchNumber", matchNumber);

    Map<String, Object> matchResults = matchMapper.getMatchById(matchParams);
    String match_name = (String) matchResults.get("match_name");
    String round_name = (String) matchResults.get("round");
    // Timestamp pay_due = (Timestamp) matchResults.get("pay_due_datetime");
    // String pay_due = payDueTimestamp.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    String pay_due = "20241217";
    String match_info = match_name + " " + round_name + " (" + matchNumber + ")";

    // 낙찰 정보 구성
    List<Map<String, Object>> bidResults = bidMapper.getMyAwardedBids(matchParams);
    System.out.println("현재 경기의 데이터 건수: " + bidResults.size());

    // 전화번호별로 정보 저장
    Map<String, String> telnoUsername = new HashMap<>();
    Map<String, Integer> telnoBidTotal = new HashMap<>();
    Map<String, StringBuilder> telnoBidsArray = new HashMap<>();

    for (Map<String, Object> result : bidResults) {
        // 값 추출
        String bid_telno = result.get("bid_telno").toString();
        String seat_no = result.get("seat_no").toString();
        int bid_amount = Integer.parseInt(result.get("bid_amount").toString());
        int total_bid_amount = Integer.parseInt(result.get("total_bid_amount").toString());
        String username = result.get("username").toString();

        // 전화번호별로 사용자 이름 저장
        telnoUsername.putIfAbsent(bid_telno, username);

        // 전화번호별로 입찰 총액
        telnoBidTotal.put(bid_telno, total_bid_amount);

        // 낙찰 내역 누적
        telnoBidsArray.putIfAbsent(bid_telno, new StringBuilder());
        telnoBidsArray.get(bid_telno).append(seat_no).append("번,  입찰액: ").append(bid_amount).append("원\n");
    }

    // 각 전화번호별로 메시지 생성 및 전송
    for (String tel : telnoBidsArray.keySet()) {
        String userName = telnoUsername.get(tel);
        String bidsArray = telnoBidsArray.get(tel).toString();
        int bidTotal = telnoBidTotal.get(tel);

        // 알림톡 템플릿에 맞춘 메시지 문자열 생성
        String message = userName + "님의 낙찰 내용을 알려드립니다.\n" +
                         "경기 : " + match_info + "\n" +
                         "좌석   입찰금액\n" +
                         "--------------------------------\n" +
                         bidsArray +
                         "--------------------------------\n" +
                         "총 결제 금액은 " + bidTotal + "원입니다.\n" +
                         "결제시한 : " + pay_due;

        System.out.println("\n알림톡 전송 메시지: \n" + message);
            
            // 전화번호별로 메시지 전송
            // sendOneAlimTalk(message, telno, ACCESS_TOKEN);
        }

    try {
        int affectedRows = matchMapper.updateMatchAlimtalkStatus(matchParams);
        if (affectedRows > 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "정보가 성공적으로 수정되었습니다.");
            return response;
        } else {
            throw new NotFoundException(null);
        }
    } catch (NotFoundException e) {
        throw e;
    } catch (Exception e) {
        throw new DataAccessException(null,e);
    }
    }
} 
// OAuth 2.0 인증 Response Syntax
// {
//     "code": "200",
//     "result": {
//       "detail_code": "NRM00000",
//       "detail_message": "성공"
//     },
//     "access_token": "example-token",
//     "token_type": "bearer",
//     "expires_in": 863999
//   }
  