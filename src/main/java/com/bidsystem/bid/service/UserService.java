package com.bidsystem.bid.service;

import com.bidsystem.bid.mapper.UserMapper;
import com.bidsystem.bid.mapper.AdminMapper;
import com.bidsystem.bid.service.ExceptionService.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private CommonAuthorization commonService;

    // 로그인 처리
    public Map<String, Object> login(Map<String, Object> request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            Map<String, Object> verifyResult = commonService.verifyPassword(request);
            // 로그인 성공 시 세션 및 쿠키 처리
            String userId = (String) verifyResult.get("userid");
            String userName = (String) verifyResult.get("username");
            String userType = (String) verifyResult.get("usertype");

            // 세션 설정
            commonService.setSessionAttributes(httpRequest.getSession(), userId, userType);

            // 쿠키 설정
            commonService.setLoginCookie( httpRequest.getSession(),httpResponse);

            // 성공 응답 반환
            Map<String, Object> response = new HashMap<>();
            response.put("message", "로그인 성공");
            response.put("userName", userName);
            response.put("userType", userType);
            return response;
        } catch (BadRequestException | NotFoundException | PasswordMismatchException e) {
            throw e;
        } catch (Exception e) {
            throw new ServerException(null,e);
        }
    }
    
    // 사용자 정보 조회
    public Map<String, Object> getUserByQuery(Map<String, Object> request) {
        try {
            String requestType = (String) request.get("requestType");
            String table = (String) request.get("table");
            Map<String, Object> results = commonService.verifyPassword(request);

            if ("user".equals(table)) {
                if ("query".equals(requestType)) {
                    return results;
                } else if ("userid".equals(requestType)) {
                    results = userMapper.getUserById(request);
                } else if ("telno".equals(requestType)) {
                    results = userMapper.getUserByTelno(request);
                } else {
                    throw new BadRequestException(null);
                }
            } else if ("admin".equals(table)) {
                if ("query".equals(requestType)) {
                    return results;
                } else if ("userid".equals(requestType)) {
                    results = adminMapper.getUserById(request);
                } else {
                    throw new BadRequestException(null);
                }
            }
   
            return results;
        } catch (BadRequestException | NotFoundException | PasswordMismatchException e) {
            throw e;
        } catch (Exception e) {
            throw new ServerException(null,e);
        }
    }
    public Map<String, Object> getUserById(Map<String, Object> request) {
        try {
            return userMapper.getUserById(request);
        } catch (Exception e) {
            throw new ServerException(null,e);
        }
    }
    public Map<String, Object> getUserByTelno(Map<String, Object> request) {
        try {
            return userMapper.getUserByTelno(request);
        } catch (Exception e) {
            throw new ServerException(null,e);
        }
    }
    // 사용자 ID 찾기
    public Map<String, Object> findUserId(Map<String, Object> request) {
        try {
            String table = (String) request.get("table");
            Map<String, Object> results = null;

            if ("user".equals(table)) {
                results = userMapper.getUserIdByEmailAndName(request);
            } else if ("admin".equals(table)) {
                results = adminMapper.getUserIdByEmailAndName(request);
            } else {
                throw new BadRequestException(null);
            }

            if (results == null || results.isEmpty()) {
                throw new NotFoundException("사용자 정보가 없습니다.");
            } else {
                return results;
            }
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }

    // 비밀번호 변경
    public void changePassword(Map<String, Object> request) {
        try {
            String table = (String) request.get("table");
            String inputPassword = (String) request.get("password");

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(inputPassword);
            request.put("password", encodedPassword);

            if ("user".equals(table)) {
                userMapper.changePassword(request);
            } else if ("admin".equals(table)) {
                adminMapper.changePassword(request);
            } else {
                throw new BadRequestException(null);
            }
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }

    // 사용자 정보 업데이트
    public void updateUser(Map<String, Object> request) {
        try {
            String table = (String) request.get("table");

            if ("user".equals(table)) {
                userMapper.updateUser(request);
            } else if ("admin".equals(table)) {
                adminMapper.updateUser(request);
            } else {
                throw new BadRequestException(null);
            }
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }

    // 사용자 등록
    public void registerUser(Map<String, Object> request) {
        try {
            String table = (String) request.get("table");
            String password = (String) request.get("password");

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(password);
            request.put("password", encodedPassword);

            if ("user".equals(table)) {
                userMapper.registerUser(request);
            } else if ("admin".equals(table)) {
                adminMapper.registerUser(request);
            } else {
                throw new BadRequestException(null);
            }
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }
}
