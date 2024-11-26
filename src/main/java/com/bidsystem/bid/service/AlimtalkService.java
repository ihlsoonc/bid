package com.bidsystem.bid.service;

import com.bidsystem.bid.mapper.MatchMapper;
import com.bidsystem.bid.mapper.BidMapper;
import com.bidsystem.bid.service.ExceptionService.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class AlimtalkService {
    @Autowired
    private MatchMapper matchMapper;
    @Autowired
    private BidMapper bidMapper;

    // RestTemplate 빈을 생성
    private RestTemplate restTemplate = new RestTemplate();

    // appl
    @Value("${alimtalk.token-url}")
    private String TOKEN_URL;

    @Value("${alimtalk.send-url}")
    private String SEND_URL;

    @Value("${alimtalk.result-url}")
    private String RESULT_URL;

    @Value("${alimtalk.bsid}")
    private String BSID;

    @Value("${alimtalk.passwd}")
    private String PASSWD;

    @Value("${alimtalk.sender-key}")
    private String SENDER_KEY;

    @Value("${alimtalk.template-code}")
    private String TEMPLATE_CODE;

    @Value("${alimtalk.country-code}")
    private String COUNTRY_CODE;

    // 필요한 경우 메서드 추가

    // // 각 필드를 final로 선언
    // private final String TOKEN_URL;
    // private final String SEND_URL;
    // private final String RESULT_URL;
    // private final String BSID;
    // private final String PASSWD;
    // private final String SENDER_KEY;
    // private final String TEMPLATE_CODE;
    // private final String COUNTRY_CODE;

    // // 생성자를 통해 초기화
    // public AlimtalkService(AlimtalkProperties alimtalkProperties) {
    //     this.TOKEN_URL = alimtalkProperties.getTokenUrl();
    //     this.SEND_URL = alimtalkProperties.getSendUrl();
    //     this.RESULT_URL = alimtalkProperties.getResultUrl();
    //     this.BSID = alimtalkProperties.getBsid();
    //     this.PASSWD = alimtalkProperties.getPasswd();
    //     this.SENDER_KEY = alimtalkProperties.getSenderKey();
    //     this.TEMPLATE_CODE = alimtalkProperties.getTemplateCode();
    //     this.COUNTRY_CODE = alimtalkProperties.getCountryCode();
    // }

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
            ResponseEntity<String> response = restTemplate.exchange(TOKEN_URL, HttpMethod.POST, entity, String.class);
    
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
            throw new ServerException("시스템 오류 : 알림톡 전송 토큰 획득에 실패하였습니다.", e);
        }
        System.out.println("getAccessToken 메서드 종료, 반환할 토큰: " + retToken);
        return retToken;
    }

    public Map<String, Object> sendAlimtalkByMatch(String matchNumber, String ACCESS_TOKEN) throws IOException {

        // 경기 정보 구성
        Map<String, Object> matchParams = new HashMap<>();
        matchParams.put("matchNumber", matchNumber);

        Map<String, Object> matchResults = matchMapper.getMatchById(matchParams);
        String match_name = (String) matchResults.get("match_name");
        String round_name = (String) matchResults.get("round");
        String match_info = match_name + " " + round_name + " (" + matchNumber + ")";

        // 결제시한 정보 구성
        String pay_due ="";
        Timestamp paydueTimestamp = (Timestamp) matchResults.get("pay_due_datetime");
        if (paydueTimestamp != null) {
            LocalDateTime paydueDatetime = paydueTimestamp.toLocalDateTime();
            pay_due = paydueDatetime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        // 낙찰 정보 구성 - 경기별 여러 사용자의 정보로 구성됨
        List<Map<String, Object>> bidResults = bidMapper.getAwardedBidsByMatch(matchParams);
        System.out.println("현재 경기의 낙찰 정보 데이터 건수: " + bidResults.size());

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

            System.out.println("\n알림톡 전송 전화번호: "+ tel+" \n\n"+  message+"\n\n");
                
            // 전화번호별로 메시지 전송
            sendOneAlimTalk(message, tel, ACCESS_TOKEN);
        }

        try {
            int affectedRows = matchMapper.updateMatchAlimtalkStatus(matchParams);
            if (affectedRows > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "정보가 성공적으로 수정되었습니다.");
                return response;
            } else {
                throw new NotFoundException("시스템 오류 : 알림톡 송신 정보 갱신에 실패하였습니다.");
            }
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    } 

public void sendOneAlimTalk(String alimMessage, String telno, String token) {   
    System.out.println("sendOneAlimTalk 메서드 시작");

    Map<String, Object> requestBody = new HashMap<>();

    String msgIdx = String.valueOf(System.currentTimeMillis());
    System.out.println("\n메시지 인덱스(msgIdx) 생성: " + msgIdx);

    // 요청 본문 구성
    requestBody.put("msgIdx", msgIdx);
    requestBody.put("countryCode", COUNTRY_CODE);
    requestBody.put("recipient", telno);
    requestBody.put("senderKey", SENDER_KEY);
    requestBody.put("message", alimMessage);
    requestBody.put("tmpltCode", TEMPLATE_CODE);
    requestBody.put("resMethod", "PUSH");

    // HTTP 헤더 구성
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("bt-token", token);

    // HttpEntity에 요청 본문과 헤더 설정
    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

    // 알림톡 전송 요청
    System.out.println("\n알림톡 전송 요청 시작");
    try{
        ResponseEntity<String> response = restTemplate.exchange(SEND_URL, HttpMethod.POST, entity, String.class);
        // 응답 상태 확인
        System.out.println("알림톡 전송 결과: " + response.getBody());
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("알림톡 전송 성공: " + response.getBody());
        } else {
            System.out.println("알림톡 전송 오류: HTTP " + response.getStatusCode());
        }
    } catch(Exception e) {
        throw new ServerException("시스템 오류 : 알림톡 전송 실패", e);

    }
}
}

    // // 3. 알림톡 결과 조회 메서드
    // public Map<String, Object> getAlimResult(String[] msgIdxArr, String token) throws IOException {
    //     HttpHeaders headers = new HttpHeaders();
    //     headers.setContentType(MediaType.APPLICATION_JSON);
    //     headers.set("bt-token", token);

    //     HttpEntity<String> entity = new HttpEntity<>(headers);
    //     ResponseEntity<String> response = restTemplate.exchange(RESULT_URL, HttpMethod.GET, entity, String.class);

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
  