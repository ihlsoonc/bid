package com.bidsystem.bid.controller;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bidsystem.bid.service.BidService;
import com.bidsystem.bid.service.ExceptionService.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(BidService.class);

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<String> handleServerException(ServerException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<String> handleDataAccessException(DataAccessException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<String> handlePasswordMismatchException(PasswordMismatchException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<String> DuplicateKeyException(DuplicateKeyException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NoDataException.class)
    public ResponseEntity<String> NoDataException(NoDataException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(ZeroAffectedRowException.class)
    public ResponseEntity<String> ZeroAffectedRowException(ZeroAffectedRowException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorizedException(UnauthorizedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(VerificationException.class)
    public ResponseEntity<String> handleVerificationException(VerificationException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(NoSuchAlgorithmException.class)
    public ResponseEntity<String> handleNoSuchAlgorithmException(NoSuchAlgorithmException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_IMPLEMENTED);
    }

    @ExceptionHandler(NurigoException.class)
    public ResponseEntity<String> handleNurigoException(NurigoException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(PgException.class)
    public ResponseEntity<String> handlePgException(PgException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    // @ExceptionHandler(IllegalArgumentException.class)
    // public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
    //     return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    // }

    @ExceptionHandler(UnsupportedEncodingException.class)
    public ResponseEntity<String> handleUnsupportedEncodingException(UnsupportedEncodingException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_IMPLEMENTED);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequestException(BadRequestException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<String> handleConflictException(ConflictException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

}
