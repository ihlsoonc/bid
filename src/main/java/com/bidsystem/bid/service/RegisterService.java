package com.bidsystem.bid.service;

import com.bidsystem.bid.service.ExceptionService.*;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.bidsystem.bid.dto.UserRegistrationDto;
import com.bidsystem.bid.repository.UserRepository;
import com.bidsystem.bid.entity.UserEntity;

@Service
public class RegisterService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    public RegisterService(UserRepository userRepository,BCryptPasswordEncoder bCryptPasswordEncoder ) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public Map<String, Object> registerProcess(UserRegistrationDto registerDTO) {

        // DTO에서 값 추출
        System.out.println("Processing registration for DTO: " + registerDTO);
    
        String username = registerDTO.getUsername();
        System.out.println("Extracted username: " + username);
    
        String password = registerDTO.getPassword();
        System.out.println("Extracted password: " + password);
    
        String email = registerDTO.getEmail();
        System.out.println("Extracted email: " + email);
    
        String telno = registerDTO.getTelno();
        System.out.println("Extracted telno: " + telno);
    
        String role = registerDTO.getRole();
        System.out.println("Extracted role: " + role);
    
        // 응답 객체 초기화
        Map<String, Object> response = new HashMap<>();
    
        // 중복 사용자 확인
        System.out.println("Checking if telno exists: " + telno);
        Boolean isExist = userRepository.existsByTelno(telno);
        if (isExist) {
            System.out.println("중복된 사용자가 있습니다.");
            throw new DuplicateKeyException("중복된 사용자가 있습니다.");
        }
    
        // UserEntity 객체 생성 및 값 설정
        UserEntity data = new UserEntity();
        data.setUsername(username);
        System.out.println("Set username in UserEntity: " + username);
    
        data.setPassword(bCryptPasswordEncoder.encode(password));
        System.out.println("Set encoded password in UserEntity.");
    
        data.setEmail(email);
        System.out.println("Set email in UserEntity: " + email);
    
        data.setTelno(telno);
        System.out.println("Set telno in UserEntity: " + telno);
    
        data.setRole(role);
        System.out.println("Set role in UserEntity: " + role);
    
        // 저장 및 ID 확인
        Long newId = userRepository.save(data).getId();
        System.out.println("User saved with ID: " + newId);
    
        // 결과 처리
        if (newId > 0) {
            response.put("message", "성공적으로 등록되었습니다.");
            System.out.println("Registration successful for telno: " + telno);
            return response;
        } else {
            System.out.println("User save failed, throwing exception.");
            throw new ZeroAffectedRowException(null);
        }
    }
    
}