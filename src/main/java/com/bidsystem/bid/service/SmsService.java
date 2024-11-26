package com.bidsystem.bid.service;
import com.bidsystem.bid.service.ExceptionService.*;

import org.springframework.stereotype.Service;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class SmsService {

    private static final String API_KEY = "NCS547958B8DCD89";   // coolsms key
    private static final String API_SECRET_KEY = "0FC78662787861FB62E9349FDA63A75B"; // coolsms secret-key
    private static final String SMS_PROVIDER = "https://api.coolsms.co.kr"; // coolsms provider
    private static final String SMS_SENDER = "024475967"; // coolsms 발신자 번호
    private static final Integer EXPIRATION_TIME_IN_MINUTES = 3;

    // 인증 코드를 저장할 HashMap (메모리 내에서 관리)
    private Map<String, Map<String, Object>> verificationCodes = new HashMap<>();

    // CoolSMS 서비스 초기화
    private DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(API_KEY, API_SECRET_KEY, SMS_PROVIDER);
    
    // 인증코드 전송
    public Map<String, Object> sendVerificationMessage(Map<String, Object> request) {
        // 인증 코드 생성
        String toTelno = (String) request.get("telno");
    
        LocalDateTime sentAt = LocalDateTime.now();
    
        Random random = new Random();
        String code = String.format("%06d", random.nextInt(1000000));
    
        // HashMap에 전화번호를 키로 사용하여 인증 코드, 생성 시각 저장
        Map<String, Object> verificationData = new HashMap<>();
        verificationData.put("code", code);
        verificationData.put("sentAt", sentAt);
        verificationCodes.put(toTelno, verificationData);
    
        // 메시지 생성 및 전송
        Message message = new Message();
        message.setFrom(SMS_SENDER);
        System.out.println("[INFO] 발신자 번호 설정: " + SMS_SENDER);
    
        message.setTo(toTelno);
        System.out.println("[INFO] 수신자 번호 설정: " + toTelno);
    
        String text = "입찰시스템 사용자 등록용 인증 코드: " + code + " (유효 시간: " + EXPIRATION_TIME_IN_MINUTES + "분)";
        message.setText(text);
        System.out.println("[INFO] 메시지 내용 설정: " + text);
    
        // CoolSMS 메시지 전송
        try {
            System.out.println("[INFO] SMS 전송 시작");
            SingleMessageSentResponse smsresponse = messageService.sendOne(new SingleMessageSendingRequest(message));
            System.out.println("[INFO] SMS 전송 응답: " + smsresponse);
    
            Map<String, Object> response = new HashMap<>();
            response.put("message", "인증 코드가 성공적으로 전송되었습니다.");
            response.put("verificationCode", code);     // 테스트 종료 후 삭제
            return response;
        } catch (Exception e) {
            System.out.println("[ERROR] 인증코드 전송 중 오류 발생");
            System.out.println("[ERROR] 오류 메시지: " + e.getMessage());
            throw new ServerException("시스템 오류 : 인증코드 전송 중 오류가 발생하였습니다. " + code, e);
        }
    }
    
    //인증 코드 검증 
    public Map<String, Object> verifyCode(Map<String, Object> request) {
        LocalDateTime verifiedAt = LocalDateTime.now();
        String code = (String) request.get("authNumber");
        String toTelno = (String) request.get("telno");
        Map<String, Object> verificationData = verificationCodes.get(toTelno);

        if (verificationData == null) {
            throw new VerificationException("시스템 오류 : 저장된 인증정보가 없습니다.");
        }

        String storedCode = (String) verificationData.get("code");
        LocalDateTime sentAt = (LocalDateTime) verificationData.get("sentAt");

        // 인증 코드가 일치하는지 확인
        if (!storedCode.equals(code)) {
            throw new VerificationException("인증코드가 일치하지 않습니다.");
        }

        // 인증 코드가 만료되었는지 확인
        if (verifiedAt.isAfter(sentAt.plusMinutes(EXPIRATION_TIME_IN_MINUTES))) {
            throw new VerificationException("인증유효시간이 지났습니다. 인증코드 재발송을 눌러주세요.");
        }

        // 인증 코드 사용 후 삭제
        verificationCodes.remove(toTelno);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "인증 코드가 일치합니다.");
        return response;
    }
}