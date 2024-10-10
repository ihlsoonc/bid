package com.bidsystem.bid.controller;
import com.bidsystem.bid.service.ExceptionService.*;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Balance;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.model.StorageType;
import net.nurigo.sdk.message.request.MessageListRequest;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.MessageListResponse;
import net.nurigo.sdk.message.response.MultipleDetailMessageSentResponse;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import net.nurigo.sdk.message.service.MessageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ch.qos.logback.core.status.ErrorStatus;
import jakarta.annotation.PostConstruct;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api/sendsms")
public class SmsController {

    private static final String API_KEY = "NCSGX5PWFCMXYXHH";
    private static final String API_SECRET_KEY = "9QBDTPZP3ABYWYMEHXGY5HMWWKADAVAL";
    private static final String SMS_PROVIDER = "https://api.coolsms.co.kr";
    private static final String SMS_SENDER = "01092355073"; // 발신자 번호
    private static final Integer EXPIRATION_TIME_IN_MINUTES = 5;

    // 인증 코드를 저장할 HashMap (메모리 내에서 관리)
    private final Map<String, Map<String, Object>> verificationCodes = new HashMap<>();

    // CoolSMS 서비스 초기화
    private final DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(API_KEY, API_SECRET_KEY, SMS_PROVIDER);

    /**
     * 인증 번호 생성 및 SMS 전송
     */
    @PostMapping("/sendauthcode")
    public ResponseEntity<Map<String, Object>> getPhoneNumberForVerification(@RequestBody Map<String, Object> request) { 
        String toTelno = (String) request.get("telno");
        LocalDateTime sentAt = LocalDateTime.now();
        String code = sendVerificationMessage(toTelno, sentAt);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "인증 코드가 성공적으로 전송되었습니다.");
        response.put("verificationCode", code);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 인증 코드 전송
     */
    private String sendVerificationMessage(String toTelno, LocalDateTime sentAt) {
        // 인증 코드 생성
        String code = UUID.randomUUID().toString().substring(0, 6); // 6자리 인증 코드 생성
        
        // HashMap에 전화번호를 키로 사용하여 인증 코드, 생성 시각 및 만료 시간 저장
        Map<String, Object> verificationData = new HashMap<>();
        verificationData.put("code", code);
        verificationData.put("sentAt", sentAt);
        verificationCodes.put(toTelno, verificationData);

        // 메시지 생성 및 전송
        Message message = new Message();
        message.setFrom(SMS_SENDER);
        message.setTo(toTelno);
        String text = "입찰시스템 사용자 등록용 인증 코드: " + code + " (유효 시간: " + EXPIRATION_TIME_IN_MINUTES + "분)";
        message.setText(text);

        // CoolSMS 메시지 전송
        // SingleMessageSentResponse response = messageService.sendOne(new SingleMessageSendingRequest(message));
        // System.out.println("SMS 전송 응답: " + response);

        // 로그 출력
        System.out.println("인증 코드 전송: " + code);
        System.out.println("전송 시간: " + sentAt);
        return code;
    }

    /**
     * 인증 코드 검증
     */
    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, Object>> verifyCode(@RequestBody Map<String, Object> request) {
        LocalDateTime verifiedAt = LocalDateTime.now();
        String code = (String) request.get("authNumber");
        String toTelno = (String) request.get("telno");
        verifyCode(toTelno, code, verifiedAt);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "정상 인증되었습니다.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void verifyCode(String toTelno, String code, LocalDateTime verifiedAt) {
        Map<String, Object> verificationData = verificationCodes.get(toTelno);

        if (verificationData == null) {
            throw new VerificationException("저장된 인증 데이터가 없습니다.");
        }

        String storedCode = (String) verificationData.get("code");
        LocalDateTime sentAt = (LocalDateTime) verificationData.get("sentAt");

        // 인증 코드가 일치하는지 확인
        if (!storedCode.equals(code)) {
            throw new VerificationException("인증에 실패했습니다. 코드가 일치하지 않습니다.");
        }

        // 인증 코드가 만료되었는지 확인
        if (verifiedAt.isAfter(sentAt.plusMinutes(EXPIRATION_TIME_IN_MINUTES))) {
            throw new VerificationException("인증 시간이 만료되었습니다. 다시 인증을 해주세요.");
        }

        // 인증 코드 사용 후 삭제
        verificationCodes.remove(toTelno);
    }
}
