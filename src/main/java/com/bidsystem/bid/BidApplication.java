package com.bidsystem.bid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class BidApplication {

    private static final Logger logger = LoggerFactory.getLogger(BidApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(BidApplication.class, args);
        logger.info("\n\n=================== BidApplication 이 시작되었습니다.\n");
    }


    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:8080", "https://stgstdpay.inicis.com", "null") // 여러 도메인 설정
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
                // CORS 설정 로깅
                logger.info("\n\n==================== BidApplication에서 CORS configuration 이 적용됩니다. ====================\n");
        };
    };
    }
}   