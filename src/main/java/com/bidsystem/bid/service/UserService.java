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
    private CertificationService certificationService;

    // 로그인 처리
    public Map<String, Object> login(Map<String, Object> request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            Map<String, Object> verifyResult = certificationService.verifyPassword(request);
            // 로그인 성공 시 세션 및 쿠키 처리
            String telno = (String) verifyResult.get("telno");
            String userId = (String) verifyResult.get("userid");
            String userName = (String) verifyResult.get("username");
            String userType = (String) verifyResult.get("usertype");

            // 세션 설정
            certificationService.setSessionAttributes(httpRequest.getSession(), userId, telno, userType,userName);

            // 쿠키 설정
            certificationService.setLoginCookie( httpRequest.getSession(),httpResponse);

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
            Map<String, Object> results = certificationService.verifyPassword(request);
            if (results == null || results.isEmpty()) {
                throw new NotFoundException(null);
            } else {
                return results;
            }
        } catch (BadRequestException | NotFoundException | PasswordMismatchException e) {
            throw e;
        } catch (Exception e) {
            throw new ServerException(null,e);
        }
    }

    // pginterface호출함수 (password verification이 없음)
    public Map<String, Object> getUserByTelno(Map<String, Object> request) {
        try {
            request.put("queryType", "telno");
            request.put("query", request.get("telno"));
            return userMapper.getUserByQuery(request);
        } catch (Exception e) {
            throw new ServerException(null,e);
        }
    }

    // 비밀번호 변경
    public Map<String, Object> changePassword(Map<String, Object> request) {
        try {
            String table = (String) request.get("table");
            String inputPassword = (String) request.get("password");
            System.out.println("\n\n++++++++++++++++++++++++"+request+"\n\n");
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(inputPassword);
            request.put("newPassword", encodedPassword);
            int affectedRows = 0;
            if ("user".equals(table)) {
                affectedRows =  userMapper.changePassword(request);
            } else if ("admin".equals(table)) {
                affectedRows =  adminMapper.changePassword(request);
            } else {
                throw new BadRequestException(null);
            }
            if (affectedRows > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "성공적으로 수행되었습니다.");
                return response;
            } else {
                throw new ZeroAffectedRowException("작업이 처리되지 않았습니다.");
            }
        } catch (NotFoundException e) {
            throw new NotFoundException(null);
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }

    // 사용자 등록
    public Map<String, Object> registerUser(Map<String, Object> request) {
        try {
            String table = (String) request.get("table");
            String password = (String) request.get("password");

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(password);
            request.put("password", encodedPassword);
            int affectedRows = 0;
            if ("user".equals(table)) {
                affectedRows =  userMapper.registerUser(request);
            } else if ("admin".equals(table)) {
                affectedRows =  adminMapper.registerUser(request);
            } else {
                throw new BadRequestException(null);
            }
            if (affectedRows > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "성공적으로 등록되었습니다.");
                return response;
            } else {
                throw new ZeroAffectedRowException(null);
            }
        } catch (BadRequestException e) {
            throw e;
        } catch (org.springframework.dao.DataAccessException e) {                  //DUPKEY를 catch하기 위함
            if (e instanceof org.springframework.dao.DuplicateKeyException) {       //DUPKEY를 catch하기 위함
                throw new DuplicateKeyException("중복된 정보입니다. 입력 내용을 확인하세요.");
            } else {
                throw new DataAccessException(null,e);
            }
        }
    }

    // 사용자 정보 업데이트
    public Map<String, Object> updateUser(Map<String, Object> request) {
        try {
            String table = (String) request.get("table");
            int affectedRows = 0;
            if ("user".equals(table)) {
                affectedRows =  userMapper.updateUser(request);
            } else if ("admin".equals(table)) {
                affectedRows =  adminMapper.updateUser(request);
            } else {
                throw new BadRequestException("tableName정보 parameter 오류입니다.");
            }
            if (affectedRows > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "성공적으로 수행되었습니다.");
                return response;
            } else {
                throw new ZeroAffectedRowException(null);
            }
        } catch (ZeroAffectedRowException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }
}
