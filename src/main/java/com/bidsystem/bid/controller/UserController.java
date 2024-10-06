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

    // 사용자 ID 조회 (이메일과 이름으로)
    @PostMapping("/find-id")
    public ResponseEntity<Map<String, Object>> findUserId(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = userService.findUserId(request);
        return ResponseEntity.ok(result); 
    }

    // 사용자 등록
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Map<String, Object> request) {
        userService.registerUser(request); // 서비스에서 사용자 등록 처리
        return ResponseEntity.ok("사용자 등록이 완료되었습니다.");
    }

    // 사용자 정보 수정
    @PostMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody Map<String, Object> request) {
        userService.updateUser(request); // 서비스에서 사용자 정보 수정 처리
        return ResponseEntity.ok("사용자 정보가 성공적으로 수정되었습니다.");
    }

    // 비밀번호 변경
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody Map<String, Object> request) {
        userService.changePassword(request); // 서비스에서 비밀번호 변경 처리
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }
}
