package com.bidsystem.bid;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class CorsLoggingFilter extends HttpFilter {

    private static final Logger logger = LoggerFactory.getLogger(CorsLoggingFilter.class);

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        String origin = request.getHeader("Origin");

        // if (origin != null) {
        //     logger.info("\n\n ------------------------------ 요청된 Origin: " + origin+"\n\n");
        // } else {
        //     logger.info("\n\nOrigin 헤더가 포함되지 않았습니다.\n\n");
        // }

        // 다음 필터 또는 서블릿 실행
        chain.doFilter(request, response);
    }
}
