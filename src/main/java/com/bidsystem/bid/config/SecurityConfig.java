package com.bidsystem.bid.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

import com.bidsystem.bid.jwt.CustomLogoutFilter;
import com.bidsystem.bid.jwt.JWTFilter;
import com.bidsystem.bid.jwt.JWTUtil;
import com.bidsystem.bid.jwt.LoginFilter;
import com.bidsystem.bid.repository.RefreshRepository;

import java.util.Arrays;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    private final AuthenticationConfiguration authConfiguration;

    @Autowired
    private   JWTUtil jwtUtil;
    private RefreshRepository refreshRepository;
    public SecurityConfig(AuthenticationConfiguration authConfiguration, JWTUtil jwtUtil, RefreshRepository refreshRepository ) {
        this.authConfiguration = authConfiguration;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
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
                .requestMatchers(mvc.pattern("/reissue-access-token")).permitAll()
                .requestMatchers(mvc.pattern("/api/pg**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/WEB-INF/views/**")).permitAll()
                // .requestMatchers(mvc.pattern("/admin")).hasRole("ADMIN")
                // .requestMatchers(new AntPathRequestMatcher("/static/**")).permitAll()
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
                    .frameOptions(frameOptions -> frameOptions.sameOrigin()) // 동일 출처에서만 iframe 허용
                    .contentSecurityPolicy(csp -> csp.policyDirectives("frame-ancestors 'self' http://localhost:5000")); // CSP 설정
            });
        http.addFilterAt(new LoginFilter(authManager(authConfiguration), jwtUtil, refreshRepository ), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new RequestLoggingFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new JWTFilter(jwtUtil) , LoginFilter.class);
        http.addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);
        return http.build();
    }

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