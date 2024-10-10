package com.bidsystem.bid.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class ExceptionService {
    private static final Logger logger = LoggerFactory.getLogger(BidService.class);

    //=======================++logging error exception ++===============================
    public static class ServerException extends RuntimeException {
        // 메시지와 예외 객체를 함께 받는 생성자
        public ServerException(String message, Throwable cause) {
            super(message != null && !message.isEmpty() ? message :"오류 : 서버에서 오류가 발생하였습니다."); 
            logger.error("\n\n++시스템 오류가 발생하였습니다.",cause.getMessage(), cause);
        }
    }
    public  static class DataAccessException extends RuntimeException {
        public DataAccessException(String message, Throwable cause) {
            super(message != null && !message.isEmpty() ? message :"오류 : 데이터베이스 접근 중 오류가 발생하였습니다."); 
            logger.error("\n\n++데이터베이스 접근 중 오류가 발행하였습니다.", cause.getMessage(), cause);
        }
    }

    public  static class IllegalStateException extends RuntimeException {
        public IllegalStateException(String message, Throwable cause) {
            super(message != null && !message.isEmpty() ? message :"오류 : 서버에서 오류가 발생하였습니다(Illegal State)."); 
            logger.error("\n\n++시스템 오류가 발생하였습니다.Illegal State", message, cause.getMessage(), cause);
        }
    }

    public  static class IllegalArgumentException extends RuntimeException {
        public IllegalArgumentException(String message, Throwable cause) {
            super(message != null && !message.isEmpty() ? message :"오류 : 서버에서 오류가 발생하였습니다(Illegal Argument).", cause); 
            logger.error("\n\n++시스템 오류가 발생하였습니다.Illegal Argument", message, cause);
        }
    }

    public static class NoSuchAlgorithmException extends RuntimeException {
        public NoSuchAlgorithmException(String message, Throwable cause) {
            super(message != null && !message.isEmpty() ? message :"오류 : 서버에서 오류가 발생하였습니다."); 
            logger.error("\n\n++알고리즘을 찾을 수 없습니다.", message, cause.getMessage(), cause);
        }
    }
    public static class UnsupportedEncodingException extends RuntimeException {
        public UnsupportedEncodingException(String message, Throwable cause) {
            super(message != null && !message.isEmpty() ? message :"오류 : 서버에서 오류가 발생하였습니다."); 
            logger.error("\n\n++Encoding중 오류가 발생하였습니다.", message, cause.getMessage(), cause);
        }
    }
    // insert에서 dupkey이외의 이유로 affctedrow ++0인 경우(DB제약조건 위반 등등
    // update에서 notfound 이외의 이유로 affctedrow ++0인 경우 (DB제약조건 위반 등등)
    // biz logic 상 dupkey, notfound이 경우가 없는데도 불구하고... 결과가 처리되지 않는 경우에 발생
    public static class ZeroAffectedRowException extends RuntimeException {
        public ZeroAffectedRowException(String message) {
            super(message != null && !message.isEmpty() ? message :"오류 : 서버에서 오류가 발생하였습니다. (noRows)"); 
            logger.error("\n\n++작업수행이 실패하였습니다. ZeroAffectedRow", message);
        }
    }

    //=======================++logging info exception ++===============================

    // DuplicateKeyException
    
    public static class DuplicateKeyException extends RuntimeException {
        private String loggingMessage; // loggingMessage 변수 선언
    
        public DuplicateKeyException(String message) {
            super(message != null && !message.isEmpty() ? message : "중복된 정보입니다. 입력내용을 확인하세요.");
            loggingMessage = message != null && !message.isEmpty() ? message : "중복된 정보입니다. 입력내용을 확인하세요.";
            logger.info("\n\n++ DuplicateKeyException: " + loggingMessage + " ++\n\n");
        }
    }

    // UnauthorizedException
    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message != null && !message.isEmpty() ? message : "권한이 없습니다.");
            logger.info("\n\n++ UnauthorizedException: " + (message != null ? message : "권한이 없습니다.") + " ++\n\n");
        }
    }

    // VerificationException
    public static class VerificationException extends RuntimeException {
        public VerificationException(String message) {
            super(message != null && !message.isEmpty() ? message : "인증이 실패하였습니다. 다시 인증해주세요.");
            logger.info("\n\n++ VerificationException: " + (message != null ? message : "인증이 실패하였습니다.") + " ++\n\n");
        }
    }

    // NotFoundException
    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message != null && !message.isEmpty() ? message : "해당되는 정보를 찾을 수 없습니다. 입력내용을 확인하세요.");
            logger.info("\n\n++ NotFoundException: " + (message != null ? message : "해당되는 정보를 찾을 수 없습니다.") + " ++\n\n");
        }
    }

    // NoDataException
    public static class NoDataException extends RuntimeException {
        public NoDataException(String message) {
            super(message != null && !message.isEmpty() ? message : "해당되는 정보가 없습니다.");
            logger.info("\n\n++ NoDataException: " + (message != null ? message : "해당되는 정보가 없습니다.") + " ++\n\n");
        }
    }
    

    // BadRequestException
    public static class BadRequestException extends RuntimeException {
        public BadRequestException(String message) {
            super(message != null && !message.isEmpty() ? message : "요청파라메터의 형식에 오류가 있습니다.");
            logger.info("\n\n++ BadRequestException: " + (message != null ? message : "요청파라메터의 형식에 오류가 있습니다.") + " ++\n\n");
        }
    }

    // ConflictException
    public static class ConflictException extends RuntimeException {
        public ConflictException(String message) {
            super(message != null && !message.isEmpty() ? message : "중복된 정보가 존재합니다.");
            logger.info("\n\n++ ConflictException: " + (message != null ? message : "중복된 정보가 존재합니다.") + " ++\n\n");
        }
    }

    // PasswordMismatchException
    public static class PasswordMismatchException extends RuntimeException {
        public PasswordMismatchException(String message) {
            super(message != null && !message.isEmpty() ? message : "비밀번호가 일치하지 않습니다.");
            // logger.info("\n\n++ PasswordMismatchException: " + (message != null ? message : "비밀번호가 일치하지 않습니다.") + " ++\n\n");
        }
    }
}
  