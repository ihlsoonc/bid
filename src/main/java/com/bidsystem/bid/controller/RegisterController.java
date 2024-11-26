package com.bidsystem.bid.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bidsystem.bid.dto.UserRegistrationDto;
import com.bidsystem.bid.service.RegisterService;

@Controller
@ResponseBody
public class RegisterController {
    private RegisterService registerService;
    public RegisterController(RegisterService registerService ) {
        this.registerService = registerService;
    }
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerProcess(UserRegistrationDto registerDTO) {
        Map<String, Object> result = registerService.registerProcess(registerDTO);
      return ResponseEntity.ok(result);
    }
    
}
