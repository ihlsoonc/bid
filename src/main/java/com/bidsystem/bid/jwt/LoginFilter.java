package com.bidsystem.bid.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.bidsystem.bid.dto.CustomUserDetails;
import com.bidsystem.bid.repository.RefreshRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authManager;
    private final JWTUtil jwtUtil;
    private static final long ACCESS_TOKEN_EXPIRATION = 2 * 60 * 1000L; // 2분 (분 * 초 * 밀리초 단위)
    private static final long REFRESH_TOKEN_EXPIRATION = 24 * 60 * 60 * 1000L; // (시간 * 분 * 초 * 밀리초 단위)
    private static final int COOKIE_MAX_AGE =  (int) (REFRESH_TOKEN_EXPIRATION /1000); //초단위

    // 생성자에서 AuthenticationManager 주입
    public LoginFilter(AuthenticationManager authManager, JWTUtil jwtUtil,RefreshRepository refreshRepository ) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
    }

    // 인증 시도 메서드 수정
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        //디버깅 용 print
        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        System.out.println("\n\nAuthentication  for request URI: " + requestUri + ", Method: " + method);

  
        String username = obtainUsername(request);
        String password = obtainPassword(request);
        // 인증 토큰 생성
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password,null);

        // 인증 관리자에 의해 인증 시도
        return authManager.authenticate(authToken);
    }
        
 protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    String username = authentication.getName();

    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
    GrantedAuthority auth = iterator.next();
    String role = auth.getAuthority();

    // 토큰 생성
    String accessToken = jwtUtil.createJwt("Authorization", username, role, ACCESS_TOKEN_EXPIRATION);
    String refreshToken = jwtUtil.createJwt("Refresh", username, role, REFRESH_TOKEN_EXPIRATION);

    // Refresh 토큰 저장: 기존 Refresh 토큰 삭제 후 새로 저장
    jwtUtil.addRefreshEntity(username, refreshToken, REFRESH_TOKEN_EXPIRATION);

    // 응답 설정
    response.addHeader("Authorization", "Bearer " + accessToken);
    response.addCookie(jwtUtil.createCookie("Refresh", refreshToken, COOKIE_MAX_AGE));
    response.setStatus(HttpStatus.OK.value());
    response.setContentType("application/json");

    // 응답 데이터를 Map으로 구성
    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put("username", username); // 사용자 이름
    responseMap.put("role", role);        // 권한
    responseMap.put("telno", customUserDetails.getTelno()); // 전화번호
    responseMap.put("refresh", refreshToken);              // Refresh 토큰

    // ObjectMapper로 Map을 JSON 문자열로 변환 후 응답에 작성
    ObjectMapper objectMapper = new ObjectMapper();
    String jsonResponse = objectMapper.writeValueAsString(responseMap);
    System.out.println("====== Loginfilter success two tokens are issued for username: " + username);

    response.getWriter().write(jsonResponse);
    response.getWriter().flush();

    }

    // 인증 실패 시 호출되는 메서드 구현
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {     
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error_code\": \"AUTH_FAILED\", \"message\": \"Authentication failed:.\"}");
        // response.getWriter().write("Authentication failed: " + request +failed.getMessage());
    }
}

