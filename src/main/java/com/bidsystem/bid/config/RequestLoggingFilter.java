package com.bidsystem.bid.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);

        logger.info("\n\n--------------------- Incoming request: "
                + request.getMethod()
                + " "
                + request.getRequestURI()
                + " Params: "
                + request + "\n");

        filterChain.doFilter(wrappedRequest, response);

        byte[] content = wrappedRequest.getContentAsByteArray();
        if (content.length > 0) {
            String requestBody = new String(content, StandardCharsets.UTF_8);
            logger.info("\n\n--------------------- Request Body:" + " "
                    + requestBody);
        }

        logger.info("\n\n================ Request ended:" + " "
                + response.getStatus() + " "
                + request.getRequestURI());
        response.setHeader("Set-Cookie", "JSESSIONID=" + request.getRequestedSessionId() + "; path=/; Secure; SameSite=None");
    }
}

