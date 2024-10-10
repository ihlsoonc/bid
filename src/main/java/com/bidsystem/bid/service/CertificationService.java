package com.bidsystem.bid.service;

import com.bidsystem.bid.mapper.UserMapper;
import com.bidsystem.bid.mapper.AdminMapper;
import com.bidsystem.bid.service.ExceptionService.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CertificationService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private UserMapper userMapper;

    
    public Map<String, Object> verifyPassword(Map<String, Object> request) throws Exception {
        String table = (String) request.get("table");
        Map<String, Object> results;

        try {
            // 사용자 또는 관리자 테이블에서 정보 가져오기
            if (table.equals("user")) {
                results = userMapper.getUserByQuery(request);

            } else if (table.equals("admin")){
                results = adminMapper.getUserByQuery(request);
            } else {
                throw new BadRequestException("잘못된 요청입니다. (TableName)");      
            }
            
            if (results == null || results.isEmpty()) {
                throw new NotFoundException(null);
            }

            String inputpassword = (String) request.get("password");
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

    // 세션 변수 설정 함수 (userType이 null이 아니면 추가)
    public void setSessionAttributes(HttpSession session, String userId, String telno, String userType) {
        session.setAttribute("userId", userId);
        session.setAttribute("telno", telno);
        if (userType != null) {
            session.setAttribute("userType", userType); // userType이 null이 아닐 경우에만 추가
        }
    }

    // 쿠키 설정 함수
    public void setLoginCookie(HttpSession session, HttpServletResponse response) {
        Cookie cookie = new Cookie("mysession.sid", session.getId());
        cookie.setHttpOnly(true);  // JavaScript에서 접근 불가하도록 설정
        cookie.setSecure(false);    // HTTPS를 사용하는 경우 true로 설정
        cookie.setPath("/");       // 모든 경로에서 쿠키 유효
        cookie.setMaxAge(60 * 60); // 1시간 동안 유효 (원하는 만료 시간으로 설정 가능)
        // cookie.setSameSite("Strict"); // 쿠키의 SameSite 속성 설정 (Strict, Lax, None 중 하나)
        response.addCookie(cookie);
    }
    
    // 쿠키 삭제 함수
    public void clearLoginCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("mysession.sid", null);
        cookie.setMaxAge(0);   // 쿠키 삭제
        cookie.setPath("/");   // 모든 경로에서 쿠키 유효
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // HTTPS 환경에서 사용
        // cookie.setSameSite("Strict");
        response.addCookie(cookie);
    }

    // 세션에서 사용자 ID를 가져오는 공통 함수
    public Map<String, Object> getSessionUserId(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 세션에서 사용자 ID를 가져옴
            String userId = (String) session.getAttribute("userId");
            String telno = (String) session.getAttribute("telno");
            String userType = (String) session.getAttribute("userType");
            if (telno != null) {
                // 사용자 ID가 존재하면 성공 응답을 반환
                response.put("status", "success");
                response.put("userId", userId);
                response.put("telno", telno);
                response.put("userType", userType);
            } else {
                // 사용자 ID가 없으면 오류 메시지 반환
                response.put("status", "error");
                response.put("message", "세션정보가 없습니다. 다시 로그인해주세요.");
            }
        } catch (Exception e) {
            throw new ServerException("세션정보 조회 중 오류가 발생하였습니다.",e);
        }

        return response;
    }

    // 세션 clear
    public void clearSession(HttpSession session){

        try {
            session.invalidate();  // 세션 무효화
        } catch (Exception e) {
            // 세션이 이미 무효화된 상태일 수 있으므로 예외를 다시 던지지 않고 로그 등으로 처리할 수 있음
            throw new ServerException("세션 무효화에서 오류가 발생하였습니다.",e);
        }
    }

    // 세션 복원 함수
    public void restoreSession(HttpServletRequest httpRequest, String telno, String userId, String userType) {
        HttpSession session = httpRequest.getSession(false);  // 이미 존재하는 세션을 가져옴, 없으면 null 반환

        if (session == null) {
            // 세션이 없으면 새로 생성
            session = httpRequest.getSession(true);
        }

        session.setAttribute("userId", userId);
        session.setAttribute("telno", telno);

        if (userType != null) {
            session.setAttribute("userType", userType);  // userType이 null이 아닐 경우에만 추가
        }
    }
}
