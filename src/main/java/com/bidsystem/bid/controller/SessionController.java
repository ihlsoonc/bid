package com.bidsystem.bid.controller;

import com.bidsystem.bid.service.CommonAuthorization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/session")
public class SessionController {

    @Autowired
    private CommonAuthorization commonService;

    // 세션 복원
    @PostMapping("/restore")
    public ResponseEntity<Map<String, Object>> restoreSession(@RequestBody Map<String, Object> requestBody, HttpServletRequest httpServletRequest) throws Exception {
        String userId = (String) requestBody.get("userId");
        String userType = (String) requestBody.get("userType");
    
        // 세션 복원 서비스 호출
        commonService.restoreSession(httpServletRequest, userId, userType);
    
        // 성공 응답 메시지 반환
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Session restored successfully");
    
        return ResponseEntity.ok(response);
    }

    // 세션에서 사용자 ID 가져오기
    @GetMapping("/getuserid")
    public Map<String, Object> getSessionUserId(HttpSession session) {
        return commonService.getSessionUserId(session);
    }

    // 로그아웃 처리
    @PostMapping("/clear")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            // 세션 무효화
     
            HttpSession session = httpRequest.getSession();
            session.invalidate();

            // 쿠키 삭제 함수 호출 (쿠키를 명시적으로 삭제하는 방법)
            commonService.clearLoginCookie(httpResponse);

            // 로그아웃 성공 응답 반환
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "로그아웃 성공");
            return ResponseEntity.ok(response); // 200 OK 응답 반환
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "로그아웃 처리 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

}





