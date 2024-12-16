package com.bidsystem.bid.controller;

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
    public ResponseEntity<Map<String, Object>> sendKakaoMessage(@RequestBody Map<String, Object> request) {
        String ACCESS_TOKEN = alimtalkService.getAccessToken();
        
        String matchNumber = (String) request.get("matchNumber");
        Map<String, Object> result = alimtalkService.sendAlimtalkByMatch(matchNumber, ACCESS_TOKEN);
        return ResponseEntity.ok(result);
    
    }
}