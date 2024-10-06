package com.bidsystem.bid.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ExceptionService {
    private static final Logger logger = LoggerFactory.getLogger(BidService.class);

    public static class ServerException extends RuntimeException {
        // 메시지와 예외 객체를 함께 받는 생성자
        public ServerException(String message, Throwable cause) {
            super(message != null && !message.isEmpty() ? message :"시스템 오류가 발생하였습니다.", cause); // 상위 클래스에 메시지와 예외의 원인을 전달
            logger.error("시스템 오류가 발생하였습니다.",cause.getMessage(), cause);
        }
    }
    public  static class DataAccessException extends RuntimeException {
        public DataAccessException(String message, Throwable cause) {
            super(message != null && !message.isEmpty() ? message :"데이터베이스 접근 중 발생하였습니다.", cause); // 상위 클래스에 메시지와 예외의 원인을 전달
            logger.error("데이터베이스 접근 중 오류가 발행하였습니다.", cause.getMessage(), cause);
        }
    }

    public  static class IllegalStateException extends RuntimeException {
        public IllegalStateException(String message, Throwable cause) {
            super(message != null && !message.isEmpty() ? message :"시스템 오류가 발생하였습니다.Illegal State", cause); // 상위 클래스에 메시지와 예외의 원인을 전달
            logger.error("시스템 오류가 발생하였습니다.Illegal State", message, cause.getMessage(), cause);
        }
    }


    public static class NoSuchAlgorithmException extends RuntimeException {
        public NoSuchAlgorithmException(String message, Throwable cause) {
            super(message != null && !message.isEmpty() ? message :"알고리즘을 찾을 수 없습니다.", cause); // 상위 클래스에 메시지와 예외의 원인을 전달
            logger.error("알고리즘을 찾을 수 없습니다.", message, cause.getMessage(), cause);
        }
    }
    public static class UnsupportedEncodingException extends RuntimeException {
        public UnsupportedEncodingException(String message, Throwable cause) {
            super(message != null && !message.isEmpty() ? message :"Encoding중 오류가 발생하였습니다.", cause); // 상위 클래스에 메시지와 예외의 원인을 전달
            logger.error("Encoding중 오류가 발생하였습니다.", message, cause.getMessage(), cause);
        }
    }

    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message != null && !message.isEmpty() ? message : "권한이 없습니다.");
        }
    }

    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message != null && !message.isEmpty() ? message : "해당되는 정보가 없습니다.");
        }
    }
    public static class BadRequestException extends RuntimeException {
        public BadRequestException(String message) {
            super(message != null && !message.isEmpty() ? message : "요청내용의 형식에 오류가 있습니다.");
        }
    }
    
    public static class DuplicateKeyException extends RuntimeException {
        public DuplicateKeyException(String message) {
            super(message != null && !message.isEmpty() ? message : "중복된 정보가 존재합니다.");
        }
    }
    
    public static class ConflictException extends RuntimeException {
        public ConflictException(String message) {
            super(message != null && !message.isEmpty() ? message : "중복된 정보가 존재합니다.");
        }
    }
    
    public static class PasswordMismatchException extends RuntimeException {
        public PasswordMismatchException(String message) {
            super(message != null && !message.isEmpty() ? message : "비밀번호가 일치하지 않습니다.");
        }
    }
    
}
    