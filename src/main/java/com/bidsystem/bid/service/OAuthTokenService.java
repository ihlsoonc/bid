package com.bidsystem.bid.service;
import com.bidsystem.bid.service.ExceptionService.*;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class OAuthTokenService {

    // RestTemplate 빈을 생성합니다
    private final RestTemplate restTemplate = new RestTemplate();

    // /getToken 엔드포인트를 호출하면 토큰을 가져옵니다.
    public String getOAuthToken() {
        String url = "https://www.biztalk-api.com/v2/auth/getToken";
        String token = "";
        try {
            // JSON 요청 객체 생성
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("bsid", "wisam");
            jsonObject.put("passwd", "b14c2d414288409ff3948159e1b9306c7c48a302");

            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Accept", "application/json");

            // HttpEntity에 JSON과 헤더 포함
            HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), headers);

            // POST 요청 전송
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            // 응답 본문을 가져와서 JSONObject로 변환
            JSONObject responseJson = new JSONObject(response.getBody());
            System.out.println("responseJson: Oauth" + responseJson);

            // 응답 코드 확인
            if (responseJson.get("responseCode") == "0000") {
                // JSON 파싱
                if ("NRM00000".equals(responseJson.get("detail_code"))) {
                    token = responseJson.getString("access_token");
                } else {
                    System.out.println("Error: " + responseJson.getString("msg"));
                }
            } else {
                String msg = responseJson.getString("msg") ;
                System.out.println("Error: Response status code " + responseJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.toString());
        }
        return token;
    }

    public String getOAuthToken2(String clientId, String clientSecret, String baseUrl) {
        try {
            // URL 설정
            String url = baseUrl + "/v2/oauth/token";

            // Basic 인증을 위한 client_id와 client_secret을 Base64로 인코딩
            String auth = clientId + ":" + clientSecret;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "Basic " + encodedAuth);

            // 바디 데이터 설정
            String body = "grant_type=client_credentials";

            // HttpEntity 생성 (요청 헤더와 바디를 포함)
            HttpEntity<String> request = new HttpEntity<>(body, headers);

            // POST 요청 보내기
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            // 응답 코드 확인 및 처리
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                return "POST request failed with status: " + response.getStatusCode();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
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