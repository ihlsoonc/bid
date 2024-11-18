package com.bidsystem.bid.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ExceptionService {
    private static final Logger logger = LoggerFactory.getLogger(BidService.class);

    //=======================\n\n++logging error exception \n\n++===============================
    public static class ServerException extends RuntimeException {
        public ServerException(String message, Throwable cause) {
            super(message != null && !message.isEmpty() ? message : "오류: 서버에서 오류가 발생하였습니다.");
            logger.error("\n\n\n\n+++++++\n ServerException: A system error occurred. " + message + " --- Cause : "+cause.getMessage(), cause);
        }
    }

    public static class DataAccessException extends RuntimeException {
        public DataAccessException(String message, Throwable cause) {
            super(message != null && !message.isEmpty() ? message : "오류: 데이터베이스 접근 중 오류가 발생하였습니다.");
            logger.error("\n\n\n\n+++++++\n DataAccessException: An error occurred while accessing the database. " + message + " --- Cause : "+cause.getMessage(), cause);
        }
    }

    public static class IllegalStateException extends RuntimeException {
        public IllegalStateException(String message, Throwable cause) {
            super(message != null && !message.isEmpty() ? message : "오류: 서버에서 오류가 발생하였습니다(Illegal State).");
            logger.error("\n\n\n\n+++++++\n IllegalStateException: A system error occurred. " + message + " --- Cause : "+cause.getMessage(), cause);
        }
    }

    public static class IllegalArgumentException extends RuntimeException {
        public IllegalArgumentException(String message, Throwable cause) {
            super(message != null && !message.isEmpty() ? message : "오류: 서버에서 오류가 발생하였습니다(Illegal Argument).");
            logger.error("\n\n\n\n\n\n+++++++\n IllegalArgumentException: A system error occurred. " + message + " --- Cause : "+cause.getMessage(), cause);
        }
    }

    public static class NoSuchAlgorithmException extends RuntimeException {
        public NoSuchAlgorithmException(String message, Throwable cause) {
            super(message != null && !message.isEmpty() ? message : "오류: 서버에서 알고리즘을 찾을 수 없습니다.");
            logger.error("\n\n\n\n+++++++\n NoSuchAlgorithmException: No such algorithm found. " + message + " --- Cause : "+cause.getMessage(), cause);
        }
    }

    public static class UnsupportedEncodingException extends RuntimeException {
        public UnsupportedEncodingException(String message, Throwable cause) {
            super(message != null && !message.isEmpty() ? message : "오류: 서버에서 인코딩 중 오류가 발생하였습니다.");
            logger.error("\n\n+++++++\n UnsupportedEncodingException: An error occurred during encoding. " + message + " --- Cause : "+cause.getMessage(), cause);
        }
    }

    public static class NurigoException extends RuntimeException {
        public NurigoException(String message, Throwable cause) {
            super(message != null && !message.isEmpty() ? message : "오류: 인증코드 전송 중 오류가 발생하였습니다.");
            logger.error("\n\n+++++++\n NurigoException: An error occurred during verification code sending. " + message + " --- Cause : "+cause.getMessage(), cause);
        }
    }

    public static class PgException extends RuntimeException {
        public PgException(String message, Throwable cause) {
            super(message != null && !message.isEmpty() ? message : "오류: 결제 요청 중 오류가 발생하였습니다.");
            logger.error("\n\n+++++++\n PgException: An error occurred during payment processing. " + message + " --- Cause : "+cause.getMessage(), cause);
        }
    }

    public static class ZeroAffectedRowException extends RuntimeException {
        public ZeroAffectedRowException(String message) {
            super(message != null && !message.isEmpty() ? message : "오류: 처리된 행이 없습니다.");
            logger.error("\n\n+++++++\n ZeroAffectedRowException: The operation failed to affect any rows.", message);
        }
    }

    //=======================logging info exception ===============================

    public static class DuplicateKeyException extends RuntimeException {

        public DuplicateKeyException(String message) {
            super(message != null && !message.isEmpty() ? message : "중복된 정보입니다. 입력내용을 확인하세요.");
            logger.info("\n\n+++++++\n UnauthorizedException: No permission. Message: " + (message != null ? message : ""));
        }
    }

    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message != null && !message.isEmpty() ? message : "권한이 없습니다.");
            logger.info("\n\n+++++++\n UnauthorizedException: No permission. Message: " + (message != null ? message : ""));
        }
    }

    public static class VerificationException extends RuntimeException {
        public VerificationException(String message) {
            super(message != null && !message.isEmpty() ? message : "인증이 실패하였습니다. 다시 인증해주세요.");
            logger.info("\n\n+++++++\n VerificationException: Verification failed. Message: " + (message != null ? message : ""));
        }
    }

    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message != null && !message.isEmpty() ? message : "해당되는 정보를 찾을 수 없습니다. 입력내용을 확인하세요.");
            logger.info("\n\n+++++++\n NotFoundException: No matching information found. Message: " + (message != null ? message : ""));
        }
    }

    public static class NoDataException extends RuntimeException {
        public NoDataException(String message) {
            super(message != null && !message.isEmpty() ? message : "해당되는 정보가 없습니다.");
            logger.info("\n\n+++++++\n NoDataException: No information available. Message: " + (message != null ? message : ""));
        }
    }

    public static class BadRequestException extends RuntimeException {
        public BadRequestException(String message) {
            super(message != null && !message.isEmpty() ? message : "요청 파라메터의 형식에 오류가 있습니다.");
            logger.info("\n\n+++++++\n BadRequestException: There is an error in the request parameters. Message: " + (message != null ? message : ""));
        }
    }

    public static class ConflictException extends RuntimeException {
        public ConflictException(String message) {
            super(message != null && !message.isEmpty() ? message : "중복된 정보가 존재합니다.");
            logger.info("\n\n+++++++\n ConflictException: Conflicting information exists. Message: " + (message != null ? message : ""));
        }
    }

    public static class PasswordMismatchException extends RuntimeException {
        public PasswordMismatchException(String message) {
            super(message != null && !message.isEmpty() ? message : "비밀번호가 일치하지 않습니다.");
            logger.info("\n\n+++++++\n PasswordMismatchException: Password does not match. Message: " + (message != null ? message : ""));
        }
    }
}
