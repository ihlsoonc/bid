package com.bidsystem.bid.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import java.util.Base64;

@RestController
public class KakaoMessageController {

//     private final String BASE_URL = "https://your_base_url/v2/send/kakao"; // base URL
//     private final String ACCESS_TOKEN = "your_access_token"; // access_token    private final RestTemplate restTemplate;

    // RestTemplate 빈 등록

        this.restTemplate = restTemplate;
    }

    @PostMapping("/send-kakao-message")
    public ResponseEntity<String> sendKakaoMessage(@RequestBody Map<String, Object> requestData) {
        // Base URL 설정
        String baseUrl = "https://{base_url}/v2/send/kakao";
        
        // Authorization Token 및 헤더 설정
        String accessToken = "{access_token}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("authorization", "Bearer " + accessToken);

        // 요청 본문 데이터 설정
        Map<String, Object> body = new HashMap<>();
        body.put("message_type", "AT");
        body.put("sender_key", requestData.get("senderKey"));   //
        body.put("cid", "202210181600001"); //?
        body.put("template_code", "TEMPLATE_001");  //
        body.put("phone_number", "01092355053");
        body.put("sender_no", "01092355073");   //
        body.put("message", "낙찰을 알리는 알림톡 메시지");
        body.put("fall_back_yn", false);    //

        // HttpEntity에 헤더와 바디 설정
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        // 요청을 보낼 URI 설정
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl);

        // POST 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                entity,
                String.class
        );

        // 응답 반환
        return ResponseEntity.ok(response.getBody());
    }



// public class OAuthTokenRequest {

//     public static void main(String[] args) {
//         try {
//             // 요청할 URL 설정
//             URL url = new URL("https://{base_url}/v2/oauth/token");
//             HttpURLConnection connection = (HttpURLConnection) url.openConnection();

//             // 요청 메서드 및 헤더 설정
//             connection.setRequestMethod("POST");
//             connection.setRequestProperty("Accept", "*/*");
//             connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

//             // Basic 인증 헤더 생성
//             String clientID = "{clientID}";
//             String clientSecret = "{clientSecret}";
//             String auth = clientID + ":" + clientSecret;
//             String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
//             connection.setRequestProperty("Authorization", "Basic " + encodedAuth);

//             // POST 데이터를 전송하기 위해 OutputStream 활성화
//             connection.setDoOutput(true);

//             // POST 데이터 작성
//             String postData = "grant_type=client_credentials";
//             try (OutputStream os = connection.getOutputStream()) {
//                 byte[] input = postData.getBytes("utf-8");
//                 os.write(input, 0, input.length);
//             }

//             // 응답 코드 확인
//             int responseCode = connection.getResponseCode();
//             System.out.println("Response Code: " + responseCode);

//             // 응답 데이터 처리 (필요 시)
//             if (responseCode == HttpURLConnection.HTTP_OK) {
//                 // 응답 스트림에서 데이터 읽기 등
//                 // 추가적인 로직 구현
//             }

//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }
// }

public class OAuthTokenService {

    public static void main(String[] args) {
        try {
            // 클라이언트 ID 및 시크릿 설정
            String clientId = "YOUR_CLIENT_ID";
            String clientSecret = "YOUR_CLIENT_SECRET";
            String baseUrl = "YOUR_BASE_URL";

            // URL 설정
            URL url = new URL(baseUrl + "/v2/oauth/token");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // 요청 메소드와 헤더 설정
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Basic 인증을 위해 client_id와 client_secret을 Base64로 인코딩
            String auth = clientId + ":" + clientSecret;
            // String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

            conn.setRequestProperty("Authorization", "Basic " + encodedAuth);

            conn.setDoOutput(true);

            // URL 인코딩된 바디 데이터 설정
            String body = "grant_type=client_credentials";
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = body.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 응답 코드 확인
            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // 응답 받기
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (java.io.BufferedReader br = new java.io.BufferedReader(
                        new java.io.InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println("Response: " + response.toString());
                }
            } else {
                System.out.println("POST request not worked");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
    @GetMapping("/sendMessage")
    public String sendMessage(
            @RequestParam String clientId,
            @RequestParam String messageType,
            @RequestParam String message,
            @RequestParam String cid,
            @RequestParam String phoneNumber) {
        
        return kakaoMessageService.sendMessage(clientId, messageType, message, cid, phoneNumber);
    }



// {
//     "code": "200",
//     "uid": "",
//     "cid": "",
//     "result": {
//       "detail_code": "NRM0000",
//       "detail_message": "성공"
//     }
//   }
  
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



// public class KakaoMessageService {

//     @Autowired
//     private RestTemplate restTemplate;

//     private final String BASE_URL = "https://your_base_url/v2/send/kakao"; // base URL
//     private final String ACCESS_TOKEN = "your_access_token"; // access_token

//     public String sendMessage(String clientId, String messageType, String message, String cid, String phoneNumber) {

//         // 헤더 설정
//         HttpHeaders headers = new HttpHeaders();
//         headers.set("Authorization", "Bearer " + ACCESS_TOKEN);
//         headers.set("Content-Type", "application/json");

//         // 바디 데이터 생성
//         Map<String, String> body = new HashMap<>();
//         body.put("client_id", clientId);
//         body.put("message_type", messageType);
//         body.put("message", message);
//         body.put("cid", cid);
//         body.put("phone_number", phoneNumber);

//         // JSON 변환
//         Gson gson = new Gson();
//         String jsonBody = gson.toJson(body);

//         // 요청 엔터티 생성
//         HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

//         // 요청 보내기
//         ResponseEntity<String> response = restTemplate.exchange(BASE_URL, HttpMethod.POST, requestEntity, String.class);

//         // 응답 결과 반환
//         return response.getBody();
//     }
// }

}