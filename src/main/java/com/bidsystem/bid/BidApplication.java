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
        logger.info("\n\n---------------------------- BidApplication started -------------------------------------\n");
    }

    //"https://stgstdpay.inicis.com"
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:9000", "http://localhost:5000", "null") // 여러 도메인 설정
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
                // CORS 설정 로깅
                logger.info("\n\n==================== BidApplication CORS configuration applied. ====================\n");
        };
    };
    }
}   