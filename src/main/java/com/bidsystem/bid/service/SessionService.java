package com.bidsystem.bid.service;

import org.springframework.stereotype.Service;

import com.bidsystem.bid.service.ExceptionService.UnauthorizedException;

import jakarta.servlet.http.HttpSession; 
import java.util.HashMap;
import java.util.Map;

@Service
public class SessionService {

    // 세션 복원
    public Map<String, Object> restoreSession(String userId, HttpSession session) throws Exception {
        Map<String, Object> response = new HashMap<>();

        if (userId == null || userId.isEmpty()) {
            throw new Exception("세션 복원을 위해 사용자 ID가 필요합니다.");
        }

        if (isValidUserId(userId)) {
            session.setAttribute("userId", userId);
            response.put("message", "Session restored successfully");
        } else {
            throw new Exception("Invalid user ID");
        }
        return response;
    }

    // 세션에서 사용자 ID 가져오기
    public Map<String, Object> getSessionUserId(HttpSession session) throws Exception {
        Map<String, Object> response = new HashMap<>();
        String userIdSession = (String) session.getAttribute("userId");
        System.out.println("세션에 저장된 userId: " + userIdSession);
        if (userIdSession != null) {
            response.put("userId", userIdSession);
            response.put("userType", session.getAttribute("userType"));
        } else {
            throw new UnauthorizedException("사용자 인증이 필요합니다. 로그인을 해주세요.");
        }
        return response;
    }

    // 로그아웃 처리
    public void userLogout(HttpSession session) throws Exception {
        if (session == null) {
            throw new Exception("세션이 존재하지 않습니다.");
        }
        session.invalidate();  // 세션 종료
    }

    // 사용자 ID 검증 (데이터베이스 조회 등으로 구현)
    private boolean isValidUserId(String userId) {
        return true;
    }
}
