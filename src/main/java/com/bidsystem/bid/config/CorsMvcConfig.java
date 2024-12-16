
/* 주요 클래스 설명 */
/* - CorsMvcConfig:
       Spring MVC에서 CORS(Cross-Origin Resource Sharing)를 설정하는 클래스
       특정 출처(origin)에서의 요청을 허용하기 위해 사용 */
       
package com.bidsystem.bid.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

    @Configuration
    public class CorsMvcConfig implements WebMvcConfigurer {
        
        @Override
        public void addCorsMappings(CorsRegistry corsRegistry) {
            corsRegistry.addMapping("/**")
                    .allowedOrigins("http://localhost:9000","http://localhost:5000",  "null");
        }
    }
