package com.bidsystem.bid.controller;

import com.bidsystem.bid.service.CertificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.Map;


@RestController
@RequestMapping("/api/session")
public class SessionController {

    @Autowired
    private CertificationService certificationService;

    // 세션 복원
    // 세션에서 사용자 ID 가져오기
    @GetMapping("/get-session-user")
    public Map<String, Object> getSessionUser(HttpSession session) {
        return certificationService.getSessionUser(session);
    }

    @PostMapping("/restore-session")
        public ResponseEntity<Map<String, Object>> restoreSession(
            @RequestBody Map<String, Object> request, HttpServletRequest httpRequest, HttpSession session, HttpServletResponse httpResponse) {
                Map<String, Object> response =  certificationService.restoreSession(request, httpRequest, session, httpResponse);
                return ResponseEntity.ok(response); // 200 OK 응답 반환

    }

    // 로그아웃 처리
    @PostMapping("/clear")
    public ResponseEntity<Map<String, Object>>  logout(HttpSession session, HttpServletResponse httpResponse) {
        // 서비스에서 세션과 쿠키 정리 후 응답 데이터를 받아옴
        Map<String, Object> response =  certificationService.clearSession(session, httpResponse);
        
        // 서비스에서 받은 응답 데이터를 그대로 반환
        return ResponseEntity.ok(response); // 200 OK 응답 반환
    }
}
