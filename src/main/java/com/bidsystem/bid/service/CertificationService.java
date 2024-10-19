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
    public void setSessionAttributes(HttpSession session, String userId, String telno, String userType, String userName) {
        session.setAttribute("userId", userId);
        session.setAttribute("telno", telno);
        session.setAttribute("userName", userName);
        if (userType != null) {
            session.setAttribute("userType", userType); // userType이 null이 아닐 경우에만 추가
        }
    }

    // 쿠키 설정 함수
    public void setLoginCookie(HttpSession session, HttpServletResponse response) {
        // HttpSession에서 세션 ID를 가져옴 (Java 표준 세션 ID)
        String sessionId = session.getId();         
    // JSESSIONID 쿠키 생성
        Cookie cookie = new Cookie("JSESSIONID", sessionId);
        cookie.setHttpOnly(true);  // JavaScript에서 접근 불가하도록 설정
        cookie.setSecure(true);    // HTTPS를 사용하는 경우 true로 설정
        cookie.setPath("/");       // 모든 경로에서 쿠키 유효
        cookie.setMaxAge(60 * 60); // 1시간 동안 유효 (원하는 만료 시간으로 설정 가능)
        response.addHeader("Set-Cookie", cookie.getName() + "=" + cookie.getValue() + "; Path=" + cookie.getPath() + "; HttpOnly; Secure; SameSite=None");
        response.addCookie(cookie);
    }
    
    // 쿠키 삭제 함수
    public void clearLoginCookie(HttpServletResponse response) {
        // JSESSIONID 쿠키를 삭제하기 위한 설정
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setMaxAge(0);   // 쿠키 만료 시간 0으로 설정하여 즉시 삭제
        cookie.setPath("/");   // 모든 경로에서 쿠키가 삭제되도록 설정
        cookie.setHttpOnly(true);  // 보안 설정: JavaScript에서 접근 불가
        cookie.setSecure(true);    // HTTPS 환경에서만 전송되도록 설정 (HTTPS 사용 시 true)
        // cookie.setSameSite("Strict");  // SameSite 설정 (옵션)
        response.addCookie(cookie);  // 응답에 쿠키를 추가하여 클라이언트에 삭제 요청
    }

    // 세션에서 사용자 ID를 가져오는 공통 함수
    public Map<String, Object> getSessionUserId(HttpSession session) {
    Map<String, Object> response = new HashMap<>();

    try {
        // 세션이 존재하는지 확인
        if (session == null || session.getAttribute("userId") == null) {
            // 세션이 만료되었거나 사용자 정보가 없는 경우
            throw new UnauthorizedException("세션이 유효하지 않습니다. 다시 로그인해주세요.");
        }

        // 세션에서 사용자 정보를 가져옴
        String userId = (String) session.getAttribute("userId");
        String telno = (String) session.getAttribute("telno");
        String userType = (String) session.getAttribute("userType");
        String userName = (String) session.getAttribute("userName");

        if (telno != null) {
            // 사용자 정보가 존재하면 성공 응답을 반환
            response.put("status", "success");
            response.put("userId", userId);
            response.put("telno", telno);
            response.put("userType", userType);
            response.put("userName", userName);
        } else {
            // 사용자 정보가 없으면 오류 메시지 반환
            throw new UnauthorizedException("세션 정보가 없습니다. 시스템 오류입니다.");
        }
    } catch (Exception e) {
        throw new ServerException("세션 정보 조회 중 오류가 발생하였습니다.", e);
    }

    return response;
}

// 세션 clear 및 JSESSIONID 쿠키 삭제
public void clearSession(HttpSession session, HttpServletResponse response) {

    try {
        // 세션 무효화
        session.invalidate();

        // JSESSIONID 쿠키 삭제
        Cookie cookie = new Cookie("JSESSIONID", null); // JSESSIONID 쿠키 이름으로 생성
        cookie.setMaxAge(0);   // 즉시 만료
        cookie.setPath("/");   // 애플리케이션 전체 경로에 적용
        cookie.setHttpOnly(true);  // 보안 설정
        cookie.setSecure(true);    // HTTPS를 사용하는 경우에만 true
        response.addCookie(cookie);  // 클라이언트에 쿠키 삭제 요청
        
    } catch (Exception e) {
        // 세션이 이미 무효화된 상태일 수 있으므로 예외를 다시 던지지 않고 로그 등으로 처리할 수 있음
        throw new ServerException("세션 무효화에서 오류가 발생하였습니다.", e);
    }
}


// 세션 복원 함수
public void restoreSession(HttpServletRequest httpRequest, String telno, String userName, String userType) {
    HttpSession session = httpRequest.getSession(false);  // 이미 존재하는 세션을 가져옴, 없으면 null 반환

    if (session == null) {
        // 세션이 없으면 새로 생성
        session = httpRequest.getSession(true);
    }

    try {
        session.setAttribute("telno", telno);
        session.setAttribute("userName", userName);

        if (userType != null) {
            session.setAttribute("userType", userType);  // userType이 null이 아닐 경우에만 추가
        }
    } catch (Exception e) {
        // 에러가 발생하면 처리 (로그 기록, 에러 메시지 반환 등)
        throw new ServerException("세션 복원 중 오류가 발생하였습니다.", e);
    }
}

}
