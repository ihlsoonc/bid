/* 주요 클래스 설명 */
/* - SecurityConfig:
       Spring Security의 필터 체인을 구성하고 인증 및 권한 관련 설정을 정의 */

/* - 주요 메서드 및 필드 설명:
    . jwtUtil:
       - JWT 관련 작업을 처리하는 유틸리티 객체

    . refreshRepository:
       - Refresh 토큰 관리에 필요한 데이터베이스 레포지토리

    . jwtProperties:
       - JWT 관련 설정 정보를 보관하는 프로퍼티 객체

    . securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc):
       - Spring Security의 필터 체인을 구성
       - 주요 설정:
         . CORS 정책 정의
         . 특정 경로에 대해 접근 제어 설정
         . 세션 상태를 STATELESS로 설정하여 세션 비활성화
         . Content Security Policy(CSP)와 X-Frame-Options 설정

    . mvc(HandlerMappingIntrospector introspector):
       - MVC 요청 매처 빌더를 생성
       - Spring MVC 요청 경로와 패턴을 매칭

    . bCryptPasswordEncoder():
       - BCrypt 해싱 알고리즘을 사용하는 비밀번호 인코더를 빈으로 등록

    . authManager(AuthenticationConfiguration authConfiguration):
       - Spring Security 인증 관리자 빈 등록

/* - 주요 필터 추가:
    1. LoginFilter:
       - 로그인 요청 처리
       - Access Token 및 Refresh Token 발급

    2. JWTFilter:
       - JWT 인증 필터
       - 요청의 토큰을 검증하여 사용자 인증

    3. CustomLogoutFilter:
       - 로그아웃 요청 처리
       - Refresh Token 삭제 및 쿠키 초기화

    4. RequestLoggingFilter:
       - 요청 정보(URI, 메서드, 헤더 등)를 로깅 */

/* - 주요 권한 제어:
    . `/login`, `/register`, `/reissue-access-token` 등 특정 엔드포인트는 모든 사용자에게 허용
    . 기타 모든 요청은 인증된 사용자만 접근 가능 */

/* - 활용 목적:
    . Spring Security를 활용한 JWT 기반 인증 및 권한 관리
    . RESTful API 및 무상태 세션 환경에서의 보안 강화 */

/* - 주의 사항:
    . 권한 제어가 정확히 설정되었는지 확인
    . CORS 정책이 클라이언트 애플리케이션과 호환되는지 점검 */


package com.bidsystem.bid.config;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import com.bidsystem.bid.repository.RefreshRepository;
import com.bidsystem.bid.service.JWTUtil;

import java.util.Arrays;
import java.util.Collections;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authConfiguration;

    @Autowired
    private   JWTUtil jwtUtil;
    private RefreshRepository refreshRepository;
    private JwtProperties jwtProperties;
    public SecurityConfig(AuthenticationConfiguration authConfiguration, JWTUtil jwtUtil, RefreshRepository refreshRepository, JwtProperties jwtProperties) {
        this.authConfiguration = authConfiguration;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        this.jwtProperties = jwtProperties;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable);
        http.cors((corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
                @Override
                public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOrigins(Arrays.asList("http://localhost:9000", "http://localhost:5000", "null"));
                    configuration.setAllowedMethods(Collections.singletonList("*"));
                    configuration.setAllowCredentials(true);
                    configuration.setAllowedHeaders(Collections.singletonList("*"));
                    configuration.setMaxAge(3600L);
                    configuration.setExposedHeaders(Arrays.asList("Authorization")); // client에 접근할 수 있는 헤더 설정
                    return configuration;
                }
            })));


          http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(mvc.pattern("/login")).permitAll()
                .requestMatchers(mvc.pattern("/register")).permitAll()
                .requestMatchers(mvc.pattern("/api/user/get-telno-count")).permitAll()
                .requestMatchers(mvc.pattern("/api/sendsms/send-auth-code")).permitAll()
                .requestMatchers(mvc.pattern("/api/sendsms/verify-code")).permitAll()             
                .requestMatchers(mvc.pattern("/reissue-access-token")).permitAll()
                .requestMatchers(mvc.pattern("/api/pg**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/WEB-INF/views/**")).permitAll()
                /* 
                * client(vue) 프로그램 build후 수행되는 path 
                */
                // .requestMatchers(new AntPathRequestMatcher("/static/**")).permitAll()`
                // .requestMatchers(new AntPathRequestMatcher("/index.html")).permitAll()
                // .requestMatchers(new AntPathRequestMatcher("/assets/**")).permitAll()
                // .requestMatchers(new AntPathRequestMatcher("/css/**")).permitAll()
                // .requestMatchers(new AntPathRequestMatcher("/icons/**")).permitAll()
                // .requestMatchers(new AntPathRequestMatcher("/images/**")).permitAll()
                // .requestMatchers(new AntPathRequestMatcher("/bidseats")).permitAll()
                // .requestMatchers(new AntPathRequestMatcher("/")).permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)         // stateless 상태로 세션관리
            )
            .headers(headers -> {
                headers
                    .frameOptions(frameOptions -> frameOptions.sameOrigin())        // 동일 출처에서만 iframe 허용(필요함) 구식브라우저??
                    .contentSecurityPolicy(csp -> csp.policyDirectives("frame-ancestors 'self' http://localhost:5000")); // CSP 설정
            });
        http.addFilterAt(new LoginFilter(authManager(authConfiguration), refreshRepository, jwtUtil, jwtProperties), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new JWTFilter(jwtUtil) , LoginFilter.class);
        http.addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);
        /* 
         *  request url,method, parameter, request body를 logging하는 용도
        */
        http.addFilterBefore(new RequestLoggingFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    //아래 Bean을 사용하지 않으면 spring에서 mvc인지 아닌지 구분할 수 없다는 오류메시지가 뜸뜸
    @Bean
    public MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

}