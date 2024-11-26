package com.bidsystem.bid.service;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bidsystem.bid.entity.RefreshEntity;
import com.bidsystem.bid.jwt.JWTUtil;
import com.bidsystem.bid.repository.RefreshRepository;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class ReissueService {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    private static final long ACCESS_TOKEN_EXPIRATION = 2 * 60 * 1000L; // 2분 (밀리초 단위)
    private static final long REFRESH_TOKEN_EXPIRATION = 24 * 60 * 60 * 1000L; // 24시간 (밀리초 단위)
    private static final int COOKIE_MAX_AGE =  (int) (REFRESH_TOKEN_EXPIRATION /1000); //초단위

    public ReissueService(JWTUtil jwtUtil, RefreshRepository refreshRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    public ResponseEntity<?> reissueAccessToken(String refresh, HttpServletResponse response) {
        Map<String, String> errorResponse = new HashMap<>();

        // Refresh 토큰 유효성 검사
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            errorResponse.put("error_code", "REFRESH_EXPIRED");
            errorResponse.put("message", "Refresh token is expired. Please login.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        // 토큰이 "Refresh"인지 확인
        String category = jwtUtil.getCategory(refresh);
        if (!"Refresh".equals(category)) {
            errorResponse.put("error_code", "REFRESH_INVALID");
            errorResponse.put("message", "Refresh token is invalid. Please login.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        // DB에 토큰 존재 여부 확인
        boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {
            errorResponse.put("error_code", "REFRESH_NOT_IN_DB");
            errorResponse.put("message", "Refresh token not found in DB. Please login.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        // 새로운 JWT 발급
        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);
        String newAccess = jwtUtil.createJwt("Authorization", username, role, ACCESS_TOKEN_EXPIRATION);
        String newRefresh = jwtUtil.createJwt("Refresh", username, role, REFRESH_TOKEN_EXPIRATION);

        // Db Refresh 토큰을 삭제하고 새로 갱신
        refreshRepository.deleteByRefresh(refresh);
        jwtUtil.addRefreshEntity(username, newRefresh, REFRESH_TOKEN_EXPIRATION);

        // 응답 설정
        response.addHeader("Authorization", "Bearer " + newAccess);
        response.addCookie(jwtUtil.createCookie("Refresh", newRefresh, COOKIE_MAX_AGE));
        response.setContentType("application/json");

        // 응답에 새로운 Refresh 토큰 포함
        Map<String, String> tokens = new HashMap<>();
        tokens.put("refresh", newRefresh);

        return ResponseEntity.status(HttpStatus.OK).body(tokens);
    }
}

