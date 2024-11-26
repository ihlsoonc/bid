package com.bidsystem.bid.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//             .allowedOrigins("http://localhost:9000", "http://localhost:5000", "null")  //개발 서버, spring server, payment호출 후 : 윗줄 혹인 이줄을 사용함
//             .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")

    @Configuration
    public class CorsMvcConfig implements WebMvcConfigurer {
        
        @Override
        public void addCorsMappings(CorsRegistry corsRegistry) {
            
            corsRegistry.addMapping("/**")
                    .allowedOrigins("http://localhost:9000","http://localhost:5000",  "null");
        }
    }
