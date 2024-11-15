package com.bidsystem.bid.controller;
import com.bidsystem.bid.service.ExceptionService.*;

import com.bidsystem.bid.service.AlimtalkService;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@RestController
@RequestMapping("/api/sendkakao")
public class KakaoAlimTalkController {
    @Autowired
    private AlimtalkService alimtalkService;

    @PostMapping("/send-kakao-message")
    public ResponseEntity<String> sendKakaoMessage(@RequestBody Map<String, Object> request) {
        System.out.println("sendKakaoMessage 메서드 시작");

        String ACCESS_TOKEN = alimtalkService.getAccessToken();
        System.out.println("액세스 토큰 획득: " + ACCESS_TOKEN);
        
        String matchNumber = (String) request.get("matchNumber");
        System.out.println("매치 번호 추출: matchNumber = " + matchNumber);
        try {
            alimtalkService.sendAlimtalkByMatch(matchNumber, ACCESS_TOKEN);
            
            System.out.println("sendKakaoMessage 메서드 종료, 모든 메시지 전송 완료");
            return ResponseEntity.ok("All kakao messages sent successfully");
        } catch (Exception e) {
            throw new ServerException( "error in alim control",e);
        }
        
    }

}