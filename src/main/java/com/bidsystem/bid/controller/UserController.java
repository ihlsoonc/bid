package com.bidsystem.bid.controller;

import com.bidsystem.bid.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;
    
    // 로그인 처리
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, Object> request, 
                                                     HttpServletRequest httpRequest, 
                                                     HttpServletResponse httpResponse) {
        Map<String, Object> result = userService.login(request, httpRequest, httpResponse); 
        return ResponseEntity.ok(result); 
    }
 
    // 사용자 정보 조회 (query로 조회)
    @PostMapping("/getinfobyquery")
    public ResponseEntity<Map<String, Object>> getUserByQuery(@RequestBody HashMap<String, Object> request) {
        Map<String, Object> result = userService.getUserByQuery(request); 
        return ResponseEntity.ok(result); 
    }

    // 사용자 등록
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = userService.registerUser(request); // 서비스에서 사용자 등록 처리
        return ResponseEntity.ok(result); 
    }

    // 사용자 정보 수정
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateUser(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = userService.updateUser(request); // 서비스에서 사용자 정보 수정 처리
        return ResponseEntity.ok(result); 
    }

    // 비밀번호 변경
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = userService.changePassword(request); // 서비스에서 비밀번호 변경 처리
        return ResponseEntity.ok(result); 
    }
}
