package com.bidsystem.bid.controller;
import com.bidsystem.bid.service.ExceptionService.*;
import com.bidsystem.bid.service.SeatPriceService;
import com.bidsystem.bid.service.SmsService;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/sendsms")
public class SmsController {
    @Autowired
    private SmsService smsService;

    @PostMapping("/sendauthcode")
    public ResponseEntity<Map<String, Object>> sendVerificationMessage(@RequestBody Map<String, Object> request) { 
        Map<String, Object> results = smsService.sendVerificationMessage(request);
        return ResponseEntity.ok(results); 
    }

    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, Object>> verifyCode(@RequestBody Map<String, Object> request) {
        Map<String, Object> results = smsService.verifyCode(request);
        return ResponseEntity.ok(results); 

    }

}
