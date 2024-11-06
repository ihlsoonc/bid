package com.bidsystem.bid.service;
import com.bidsystem.bid.service.ExceptionService.*;

import org.apache.tomcat.util.http.parser.Authorization;
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
    private static final String BIZTALK_TEST = "http://www.biztalk-center.co.kr/biztalkapi_test/";
    private static final String BIZTALK_RUN = "http://www.biztalk-center.co.kr/biztalkapi/";
    // private static final String CLIENT_SECRET = "66fd8d80344a5bb4b28f85ee02cd378178bacf8e"; // 실제 clientSecret로 대체

    private static final String BASE_URL = "https://bizmsg-web.kakaoenterprise.com/v2/oauth/token";
    private static final String CLIENT_ID = "wisam"; // 실제 clientID로 대체
    private static final String CLIENT_SECRET = "66fd8d80344a5bb4b28f85ee02cd378178bacf8e"; // 실제 clientSecret로 대체

    public String getAccessToken() {
        String url = BASE_URL + "/v2/oauth/token";

        // Authorization 헤더 설정
        String auth = CLIENT_ID + ":" + CLIENT_SECRET;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedAuth);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Accept", "application/json");

        // 요청 본문 설정
        String requestBody = "grant_type=client_credentials";
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            // 응답 처리
            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject jsonResponse = new JSONObject(response.getBody());
                String code = jsonResponse.getString("code");
                JSONObject result = jsonResponse.getJSONObject("result");
                String detailCode = result.getString("detail_code");
                String detailMessage = result.getString("detail_message");

                if ("200".equals(code) && "NRM00000".equals(detailCode)) {
                    String accessToken = jsonResponse.getString("access_token");
                    int expiresIn = jsonResponse.getInt("expires_in");

                    System.out.println("Access Token: " + accessToken);
                    System.out.println("Expires In: " + expiresIn + " seconds");
                    System.out.println("Message: " + detailMessage);

                    return accessToken;
                } else {
                    System.out.println("Failed: " + detailMessage);
                }
            } else {
                System.out.println("Error: HTTP " + response.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
        // String url = "https://www.biztalk-api.com/v2/auth/getToken";
        //     jsonObject.put("bsid", "wisam");
        //     jsonObject.put("passwd", "b14c2d414288409ff3948159e1b9306c7c48a302");

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
  