package com.bidsystem.bid.service;
import com.bidsystem.bid.service.ExceptionService.*;

import ch.qos.logback.core.status.ErrorStatus;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoUnknownException;
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
//     쿨에스엠에스 API key : NCS547958B8DCD89
// API SECRET: 0FC78662787861FB62E9349FDA63A75B
    private static final String API_KEY = "NCSGX5PWFCMXYXHH";
    private static final String API_SECRET_KEY = "75HQCRU4QOHYMMHRTWYC8Q8NGD8BOIH8";
    private static final String SMS_PROVIDER = "https://api.coolsms.co.kr";
    private static final String SMS_SENDER = "01092355073"; // 발신자 번호
    private static final Integer EXPIRATION_TIME_IN_MINUTES = 3;

    // 인증 코드를 저장할 HashMap (메모리 내에서 관리)
    private final Map<String, Map<String, Object>> verificationCodes = new HashMap<>();

    // CoolSMS 서비스 초기화
    private final DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(API_KEY, API_SECRET_KEY, SMS_PROVIDER);

    /**
     * 인증 코드 전송
     */
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
        message.setTo(toTelno);
        String text = "입찰시스템 사용자 등록용 인증 코드: " + code + " (유효 시간: " + EXPIRATION_TIME_IN_MINUTES + "분)";
        message.setText(text);

        // CoolSMS 메시지 전송
        try {
            SingleMessageSentResponse smsresponse = messageService.sendOne(new SingleMessageSendingRequest(message));
            System.out.println("SMS 전송 응답: " + smsresponse);
                // 로그 출력
                System.out.println("인증 코드 전송: " + code);
                System.out.println("전송 시간: " + sentAt);
                Map<String, Object> response = new HashMap<>();
                response.put("message", "인증 코드가 성공적으로 전송되었습니다.");
                response.put("verificationCode", code);
                return response;
        } catch (Exception e) {
            throw new ServerException("인증코드 전송 중 오류가 발생하였습니다. : "+code,e);
        }
    }

    public Map<String, Object> verifyCode(Map<String, Object> request) {
        LocalDateTime verifiedAt = LocalDateTime.now();
        String code = (String) request.get("authNumber");
        String toTelno = (String) request.get("telno");
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
        Map<String, Object> response = new HashMap<>();
        response.put("message", "인증 코드가 일치합니다.");
        return response;
    }
}