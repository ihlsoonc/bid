/* 주요 클래스 설명 */
/* - JWTFilter:
       Spring Security의 OncePerRequestFilter를 확장한 클래스
       각 HTTP 요청에서 JWT(Access Token)를 검증하여 사용자 인증 상태를 설정 */

/* - 주요 필드:
       . jwtUtil: JWT 유틸리티 클래스, 토큰 검증 및 정보 추출

/* - 주요 메서드:
       . isExcludedURI(HttpServletRequest request):
         - 특정 URI 경로를 제외하여 필터를 실행하지 않음
         - 로그인, 회원가입, 정적 파일 요청 등

       . doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain):
         - 요청 헤더에서 Access Token을 추출하여 유효성을 검증
         - 토큰에서 사용자 정보(username, role) 추출
         - Spring Security 인증 객체 생성 및 컨텍스트에 설정
         - 예외 상황(토큰 만료, 유효하지 않음 등) 시 적절한 HTTP 응답 반환 */

/* - 동작 과정:
       1. 특정 URI가 제외 대상인지 확인 (`isExcludedURI`)
       2. Access Token 검증:
          - null 확인 및 "Bearer " 접두어 제거
          - 만료 여부, 카테고리 확인
       3. 토큰에서 사용자 정보 추출:
          - username, role
       4. Spring Security 인증 객체 생성:
          - `CustomUserDetails` 및 `UsernamePasswordAuthenticationToken` 사용
          - `SecurityContextHolder`에 설정 */

/* - 주요 예외 처리:
       . Access Token 누락, 만료, 유효하지 않음 등에 대해
         HTTP 401 상태 코드와 JSON 응답 반환 */

/* - 활용 목적:
       . JWT 기반 인증을 통해 각 요청에 대해 사용자 식별 및 인증 */

package com.bidsystem.bid.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bidsystem.bid.dto.CustomUserDetails;
import com.bidsystem.bid.entity.UserEntity;
import com.bidsystem.bid.service.JWTUtil;

import io.jsonwebtoken.ExpiredJwtException;

import java.io.IOException;

@Component
public class JWTFilter<Authentication> extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JWTFilter.class);
    private JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    //-------------------------------------------------------------------------------------------------------------
    //security filter chain에 shouldnotinclude overrride해도 jwt filter가 수행되는 경우가 있어 여기에 기술해 줌
    //-------------------------------------------------------------------------------------------------------------
    private boolean isExcludedURI(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return 
            requestURI.contains("/api/login") ||
            requestURI.contains("/api/register") ||     
            requestURI.contains("/api/pgstart") || 
            requestURI.contains("/api/pgreturn") || 
            requestURI.contains("/api/pgstart-mobile") || 
            requestURI.contains("/api/pgreturn-mobile") || 
            requestURI.contains("/WEB-INF/views/") ||
            requestURI.contains("/static/") ||
            requestURI.contains("/index.html") ||
            requestURI.contains("/assets/") ||
            requestURI.contains("/css/") ||
            requestURI.contains("/icons/") ||
            requestURI.contains("/images/") ||
            requestURI.contains("/bidseats") ||
            requestURI.contains("/reissue-access-token") || 
            requestURI.equals("/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (isExcludedURI(request)) {
            logger.info("\nJwt filter is excluded forURL: {}", request.getRequestURL());
            // 제외 대상 URI인 경우, 필터 체인을 바로 진행하고 종료
            filterChain.doFilter(request, response);
            return;
        }
        
        String accessToken = request.getHeader("Authorization");
        if (accessToken == null) {
            logger.warn("Access Token null - Request URL: {}", request.getRequestURL());        // null인 경우는 예외사항으로 logging함
            filterChain.doFilter(request, response);
            return;
        }

        // Bearer 부분 제거 후 순수 토큰만 획득
        String token = accessToken.split(" ")[1];

        try {
            jwtUtil.isExpired(token);
        } catch (ExpiredJwtException e) {
            // response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error_code\": \"ACCESS_EXPIRED\", \"message\": \"access token expired.\"}");
            return;
        }

        // 토큰이 access인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(token);

        if (!category.equals("Authorization")) {    
            logger.warn("Access Token null - Request URL: {}", request.getRequestURL());        // invalid인인 경우는 예외사항으로 logging함
            // response body
            response.setContentType("application/json");
            response.getWriter().write("{\"error_code\": \"ACCESS_INVALID\", \"message\": \"invalid access token.\"}");

            // response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 토큰에서 username과 role 획득
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        // userEntity를 생성하여 값 set
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword("any");      // 값은 의미가 없음
        userEntity.setRole(role);

        // UserDetails에 회원 정보 객체 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

        // 스프링 시큐리티 인증 토큰 생성
        Authentication authToken = (Authentication) new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        // 세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication((org.springframework.security.core.Authentication) authToken);

        filterChain.doFilter(request, response);
    }
}
