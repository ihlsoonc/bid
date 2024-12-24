/**
 * 주요 클래스 설명:
 * - SecurityConfig:
 *   Spring Security의 필터 체인을 구성하고 인증 및 권한 관련 설정을 정의
 */

/**
 * 주요 필드 및 메서드 설명:
 * 1. jwtUtil:
 *    - JWT 관련 작업을 처리하는 유틸리티 객체
 * 2. refreshRepository:
 *    - Refresh 토큰 관리에 필요한 데이터베이스 레포지토리
 * 3. jwtProperties:
 *    - JWT 관련 설정 정보를 보관하는 프로퍼티 객체
 * 4. securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc):
 *    - Spring Security의 필터 체인을 구성
 *    - 주요 설정:
 *      . CORS 정책 정의
 *      . 특정 경로에 대해 접근 제어 설정
 *      . 세션 상태를 STATELESS로 설정하여 세션 비활성화
 *      . Content Security Policy(CSP)와 X-Frame-Options 설정
 * 5. mvc(HandlerMappingIntrospector introspector):
 *    - MVC 요청 매처 빌더를 생성
 *    - Spring MVC 요청 경로와 패턴을 매칭
 * 6. bCryptPasswordEncoder():
 *    - BCrypt 해싱 알고리즘을 사용하는 비밀번호 인코더를 빈으로 등록
 * 7. authenticationManager(AuthenticationConfiguration authConfiguration):
 *    - Spring Security 인증 관리자 빈 등록
 */

/**
 * 주요 필터 추가:
 * 1. LoginFilter:
 *    - 로그인 요청 처리
 *    - Access Token 및 Refresh Token 발급
 * 2. JWTFilter:
 *    - JWT 인증 필터
 *    - 요청의 JWT 토큰을 검증하여 사용자 인증
 * 3. CustomLogoutFilter:
 *    - 로그아웃 요청 처리
 *    - Refresh Token 삭제 및 쿠키 초기화
 * 4. RequestLoggingFilter:
 *    - 요청 정보(URI, 메서드, 헤더 등)를 로깅
 */

/**
 * 주요 권한 제어:
 * - `/login`, `/register`, `/reissue-access-token` 등 특정 엔드포인트는 모든 사용자에게 허용
 * - 기타 모든 요청은 인증된 사용자만 접근 가능
 */

/**
 * 활용 목적:
 * - Spring Security를 활용한 JWT 기반 인증 및 권한 관리
 * - RESTful API 및 무상태 세션 환경에서의 보안 강화
 */

/**
 * 주의 사항:
 * - 권한 제어가 정확히 설정되었는지 확인
 * - CORS 정책이 클라이언트 애플리케이션과 호환되는지 점검
 */

 package com.bidsystem.bid.config;

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
 import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
 
 import com.bidsystem.bid.repository.RefreshRepository;
 import com.bidsystem.bid.service.JWTUtil;
 
 import java.util.Arrays;
 import java.util.Collections;
 
 @Configuration
 @EnableWebSecurity
 public class SecurityConfig {
 
    // 주요 필드 및 생성자

    // AuthenticationConfiguration: Spring Security에서 인증 관리와 관련된 설정을 제공
    private final AuthenticationConfiguration authConfiguration;

    // JwtProperties: JWT 관련 설정(토큰 만료 시간, 키 등)을 담고 있는 프로퍼티 객체
    private final JwtProperties jwtProperties;

    // JWTUtil: JWT 생성 및 검증, 토큰 처리 로직을 담당하는 유틸리티 클래스
    private final JWTUtil jwtUtil;

    // RefreshRepository: Refresh 토큰의 데이터베이스 저장소
    private final RefreshRepository refreshRepository;

    public SecurityConfig(
            AuthenticationConfiguration authConfiguration,
            JwtProperties jwtProperties,
            JWTUtil jwtUtil,
            RefreshRepository refreshRepository) {
        this.authConfiguration = authConfiguration;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        this.jwtProperties = jwtProperties;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        // CSRF 보호 비활성화
        http.csrf(AbstractHttpConfigurer::disable)
            // HTTP Basic 인증 비활성화
            .httpBasic(AbstractHttpConfigurer::disable)
            // Form-based 로그인 비활성화
            .formLogin(AbstractHttpConfigurer::disable);

        // CORS 설정
        http.cors(cors -> cors.configurationSource(request -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(Arrays.asList("http://localhost:9000", "http://localhost:5000", "null"));
            configuration.setAllowedMethods(Collections.singletonList("*"));
            configuration.setAllowCredentials(true);
            configuration.setAllowedHeaders(Collections.singletonList("*"));
            configuration.setMaxAge(3600L);
            configuration.setExposedHeaders(Arrays.asList("Authorization")); // 클라이언트에 노출할 헤더
            return configuration;
        }));
 
        // 권한 및 인증 설정
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(mvc.pattern("/login")).permitAll()
                .requestMatchers(mvc.pattern("/register")).permitAll()
                .requestMatchers(mvc.pattern("/api/user/get-telno-count")).permitAll()
                .requestMatchers(mvc.pattern("/api/user/get-email-count")).permitAll()
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
        );
 
        // 세션 관리 설정
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // 보안 헤더 설정
        http.headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin()) // 동일 출처에서만 iframe 허용
                .contentSecurityPolicy(csp -> csp.policyDirectives("frame-ancestors 'self' http://localhost:5000"))
        );

        // 필터 추가
        http.addFilterAt(new LoginFilter(authenticationManager(authConfiguration), refreshRepository, jwtUtil, jwtProperties), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);
        http.addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);
        http.addFilterBefore(new RequestLoggingFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
     }
 
     // MVC 요청 매처 빌더 등록
     @Bean
     public MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
         return new MvcRequestMatcher.Builder(introspector);
     }
 
     // BCrypt 비밀번호 인코더 빈 등록
     @Bean
     public BCryptPasswordEncoder bCryptPasswordEncoder() {
         return new BCryptPasswordEncoder();
     }
 
     // 인증 관리자 빈 등록
     @Bean
     public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
         return authConfiguration.getAuthenticationManager();
     }
 }
 