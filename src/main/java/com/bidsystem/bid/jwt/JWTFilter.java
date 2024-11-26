package com.bidsystem.bid.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bidsystem.bid.dto.CustomUserDetails;
import com.bidsystem.bid.entity.UserEntity;

import io.jsonwebtoken.ExpiredJwtException;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class JWTFilter<Authentication> extends OncePerRequestFilter {

    private JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    //security filter chain에 shouldnotinclude overrride해도 jwt filter가 수행되는 경우가 있어 여기에 기술해줌
    private boolean isExcludedURI(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return requestURI.contains("/api/pgstart") || 
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
        System.out.println("\n\n------------- Jwt filter is excluded - Request URL: " + request.getRequestURL());
        // 제외 대상 URI인 경우, 필터 체인을 바로 진행하고 종료
        filterChain.doFilter(request, response);
        return;
    }
        String accessToken = request.getHeader("Authorization");
        if (accessToken == null) {
            System.out.println("\n\n------------- Access Token null - Request URL: " + request.getRequestURL());
            filterChain.doFilter(request, response);
            return;
        }

		//Bearer 부분 제거 후 순수 토큰만 획득
        String token = accessToken.split(" ")[1];

        try {
            jwtUtil.isExpired(token);
        } catch (ExpiredJwtException e) {
            System.out.println("\n\n----------- Access Token is expired - Request URL: " + request.getRequestURL());
            
            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error_code\": \"TOKEN_EXPIRED\", \"message\": \"access token expired.\"}");
            return;
        }

        // 토큰이 access인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(token);

        if (!category.equals("Authorization")) {

            //response body
            response.setContentType("application/json");
            response.getWriter().write("{\"error_code\": \"TOKEN_INVALID\", \"message\": \"invalid access token.\"}");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

		//토큰에서 username과 role 획득
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);
				
		//userEntity를 생성하여 값 set
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword("temppassword");
        userEntity.setRole(role);
				
		//UserDetails에 회원 정보 객체 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

		//스프링 시큐리티 인증 토큰 생성
        Authentication authToken = (Authentication) new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
		//세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication((org.springframework.security.core.Authentication) authToken);

        filterChain.doFilter(request, response);
    }
}
