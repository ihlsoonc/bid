package com.bidsystem.bid.controller;
import com.bidsystem.bid.service.ExceptionService.*;


import com.bidsystem.bid.service.AlimtalkService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/sendkakao")

public class KakaoAlimTalkController {
    @Autowired
    private AlimtalkService alimtalkService;

    @PostMapping("/send-kakao-message")
    public ResponseEntity<String> sendKakaoMessage(@RequestBody Map<String, Object> request) {
        String ACCESS_TOKEN = alimtalkService.getAccessToken();
        
        String matchNumber = (String) request.get("matchNumber");
        try {
            alimtalkService.sendAlimtalkByMatch(matchNumber, ACCESS_TOKEN);
            return ResponseEntity.ok("알림톡이 성공적으로 발송되었습니다.");
        } catch (Exception e) {
            throw new ServerException( "시스템 오류 : 알림톡 전송에 실패하였습니다.",e);
        }
        
    }

}