package com.bidsystem.bid.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CounterService {

    // AtomicInteger로 스레드 안전한 카운터 생성
    private final AtomicInteger counter = new AtomicInteger(0);

    /**
     * 현재 카운터 값을 반환
     */
    public int getCounter() {
        return counter.get(); 
    }

    /**
     * 카운터 값을 1 증가시키고 반환
     */
    public int incrementCounter() {
        return counter.incrementAndGet(); 

    }
    /**
     * 카운터 값을 0으로 초기화
     */
    public void resetCounter() {
        counter.set(0);
    }
}

