package com.bidsystem.bid.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bidsystem.bid.service.ReissueService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@ResponseBody
public class ReissueController {

    private final ReissueService reissueService;

    public ReissueController(ReissueService reissueService) {
        this.reissueService = reissueService;
    }

    @PostMapping("/reissue-access-token")
    public ResponseEntity<?> reissueAccessToken(
            @RequestBody Map<String, Object> requestBody,
            HttpServletRequest request,
            HttpServletResponse response) {

        // 요청 본문에서 "refresh" 값 가져오기
        String refresh = (String) requestBody.get("refresh");

        if (refresh == null || refresh.isEmpty()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error_code", "REFRESH_NULL");
            errorResponse.put("message", "Refresh token not found in request body.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        return reissueService.reissueAccessToken(refresh, response);
    }
}
