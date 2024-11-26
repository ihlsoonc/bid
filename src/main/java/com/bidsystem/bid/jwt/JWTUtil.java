package com.bidsystem.bid.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bidsystem.bid.entity.RefreshEntity;
import com.bidsystem.bid.repository.RefreshRepository;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;

@Component
public class JWTUtil {

    private SecretKey secretKey;
    private RefreshRepository refreshRepository;

    public JWTUtil( RefreshRepository refreshRepository) {
        //토큰 생성용 key
        String secret = "FA;1@Ml9demE7lEwK#zc3v5X!d29yM6s8@j#DYSycrhk0fYm1HdU!TyGv14SSIpdf2304147";
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.refreshRepository = refreshRepository;
    }
    public String getCategory(String token) {
      
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }
    public String getUsername(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String createJwt(String category, String username, String role, Long expiredMs) {

        return Jwts.builder()
                .claim("category", category)
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Refresh 토큰 정보를 DB에 저장하는 메서드
     * 
     * @param username 사용자 이름
     * @param refresh  Refresh 토큰
     * @param expiredMs 만료 시간 (밀리초)
     */
    public void addRefreshEntity(String username, String refresh, Long expiredMs) {
        Date expirationDate = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(expirationDate);
        refreshRepository.save(refreshEntity);
    }

    /**
     * 주어진 key와 value로 Cookie를 생성하는 메서드
     * 
     * @param key   쿠키 이름
     * @param value 쿠키 값
     * @return 생성된 Cookie 객체
     */
    public Cookie createCookie(String key, String value, int maxAge) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(maxAge); // 초 단위로 설정
        cookie.setHttpOnly(true);
        // cookie.setSecure(true); // HTTPS 환경에서 사용
        // cookie.setPath("/"); // 필요에 따라 경로 설정
        return cookie;
    }
}