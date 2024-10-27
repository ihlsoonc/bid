package com.bidsystem.bid.controller;
import com.bidsystem.bid.service.OAuthTokenService;
import com.bidsystem.bid.service.UserService;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.HashMap;
import java.util.Map;
import java.util.Base64;

@RestController
@RequestMapping("/api/sendkakao")
public class KakaoAlimTalkController {

    private final String BASE_URL = "https://www.biztalk-api.com"; // base URL
//     private final String ACCESS_TOKEN = "your_access_token"; // access_token    private final RestTemplate restTemplate;

    @Autowired
    private OAuthTokenService oAuthTokenService;
    private static final RestTemplate restTemplate = null;

    @PostMapping("/send-kakao-message")
    public ResponseEntity<String> sendKakaoMessage(@RequestBody Map<String, Object> requestData) {
        // Base URL 설정
        String url = BASE_URL + "/v2/send/kakao";
        // Authorization Token 및 헤더 설정
        // String accessToken = oAuthTokenService.getOAuthToken(clientId, clientSecret, baseUrl);

        String accessToken = oAuthTokenService.getOAuthToken();
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
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

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

}
    // @GetMapping("/sendMessage")
    // public String sendMessage(
    //         @RequestParam String clientId,
    //         @RequestParam String messageType,
    //         @RequestParam String message,
    //         @RequestParam String cid,
    //         @RequestParam String phoneNumber) {
        
    //     return kakaoMessageService.sendMessage(clientId, messageType, message, cid, phoneNumber);
    // }

// {
//     "code": "200",
//     "uid": "",
//     "cid": "",
//     "result": {
//       "detail_code": "NRM0000",
//       "detail_message": "성공"
//     }
//   }
  

