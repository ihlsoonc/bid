package com.bidsystem.bid.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import static org.springframework.security.config.Customizer.withDefaults;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class); // 로거 생성

    @SuppressWarnings("removal")
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        logger.info("\n\n================ SecurityConfig.java SecurityFilterChain started.\n");

        http
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests((authorize) -> {
                    authorize
                        .requestMatchers(mvc.pattern("/login")).permitAll()
                        .requestMatchers(mvc.pattern("/api/**")).permitAll()
                        .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/**")).permitAll()
                        .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/WEB-INF/views/**")).permitAll()
                        .anyRequest().authenticated()
                        .logout((logout) -> logout
                            .logoutSuccessUrl("/login")
                            .invalidateHttpSession(true))
                            sessionManagement(session -> session
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        );
                    logger.info("\n\n================  SecurityConfig.java Authorization rules applied.\n");
                }
            )
            .httpBasic(withDefaults());  // 기본 HTTP 인증 설정

        // X-Frame-Options 설정 추가
        http.headers(headers -> headers.frameOptions().sameOrigin()); // 동일 출처에서만 iframe 허용 -- 지정하지 않으면 결제창을 닫는 경우(pgclose에서) 오류 발생

        // Content-Security-Policy 설정 추가 (위와 관련된 선택 사항)
        http.headers(headers -> headers.contentSecurityPolicy("frame-ancestors 'self' http://localhost:5000"));

        http.addFilterBefore(new CustomLoggingFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build(); 
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }


    // 요청 정보를 출력하는 필터 클래스 정의
    public class CustomLoggingFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {

            // HttpServletRequest를 ContentCachingRequestWrapper로 감싸서 요청 본문을 캐싱
            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);

            // 필터 시작 시 바로 로그 출력
            logger.info("\n\n================ Incoming request: "+ request.getMethod()+" "+ request.getRequestURI()+ " Params: "+request.getQueryString()+"\n\n");

            // 필터 체인을 통해 다음 필터로 요청 넘기기
            filterChain.doFilter(wrappedRequest, response);

            // 요청 본문 출력 (POST/PUT 요청에 해당)
            byte[] content = wrappedRequest.getContentAsByteArray();
            if (content.length > 0) {
                String requestBody = new String(content, StandardCharsets.UTF_8);
                logger.info("\n\n================ Request Body: "+requestBody +"\n");
            }

            // 응답 처리 후 로그 출력
            logger.info("\n\n================ Request ended: "+response.getStatus()+" "+request.getRequestURI()+"\n\n");
            response.setHeader("Set-Cookie", "JSESSIONID=" + request.getRequestedSessionId() + "; path=/; Secure; SameSite=None");
        }
    }
}
