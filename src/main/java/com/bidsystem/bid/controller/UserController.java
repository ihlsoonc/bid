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
 
    // 사용자 정보 조회 (query로 조회 : query값과 queryType(telno, userid, email)을 파라메터로 함)
    @PostMapping("/getinfo-byquery")
    public ResponseEntity<Map<String, Object>> getUserByQuery(@RequestBody HashMap<String, Object> request) {
        Map<String, Object> result = userService.getUserByQuery(request); 
        return ResponseEntity.ok(result); 
    }

     
    // 사용자 정보 조회 (query로 조회는 동일하나 password verification이 있음)
    @PostMapping("/getinfo-byquery-and-password")
    public ResponseEntity<Map<String, Object>> getUserByQueryAndPassword(@RequestBody HashMap<String, Object> request) {
        Map<String, Object> result = userService.getUserByQueryAndPassword(request); 
        return ResponseEntity.ok(result); 
    }

    // 중복확인을 위한 email count
    @PostMapping("/get-email-count")
    public ResponseEntity<Map<String, Object>> getEmailCount(@RequestBody HashMap<String, Object> request) {
        Map<String, Object> result = userService.getEmailCount(request); 
        return ResponseEntity.ok(result); 
    }

    // 중복확인을 위한 telno count
    @PostMapping("/get-telno-count")
    public ResponseEntity<Map<String, Object>> getTelnoCount(@RequestBody HashMap<String, Object> request) {
        Map<String, Object> result = userService.getTelnoCount(request); 
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
