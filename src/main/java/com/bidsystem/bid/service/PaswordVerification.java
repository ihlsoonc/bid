package com.bidsystem.bid.service;

import com.bidsystem.bid.mapper.UserMapper;
import com.bidsystem.bid.service.ExceptionService.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PaswordVerification {

    @Autowired
    private UserMapper userMapper;

   
    // 사용자 테이블에서 id와 password로 자격 확인 
    public Map<String, Object> verifyPassword(Map<String, Object> request) throws Exception {
        Map<String, Object> results;
        try {
            // 사용자 정보 가져오기
            results = userMapper.getUserByQuery(request);
            if (results == null || results.isEmpty()) {
                throw new NotFoundException(null);
            }

            String inputpassword = (String) request.get("password");   // 입력된 비밀번호
            String encodedPassword = (String) results.get("password"); // 테이블에 저장된 암호화된 비밀번호

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (passwordEncoder.matches(inputpassword, encodedPassword)) {
                return results;  
            } else {
                throw new PasswordMismatchException(null);  
            }
        } catch (BadRequestException | NotFoundException | PasswordMismatchException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }

}
