package com.bidsystem.bid.service;

import com.bidsystem.bid.service.ExceptionService.*;
import com.bidsystem.bid.mapper.SeatPriceMapper;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Service
public class SeatPriceService {

    @Autowired
    private SeatPriceMapper seatPriceMapper;

    // 좌석별 가격 조회
    public List<Map<String, Object>> getSeatPrices(Map<String, Object> params) {
        try {
            return seatPriceMapper.getSeatPrice(params);
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }

    // 여러 좌석 정보 갱신
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updateSeatPriceArray(Map<String, Object> params) {
        try {
            int affectedRows = seatPriceMapper.updateSeatPriceArray(params);
            Map<String, Object> response = new HashMap<>();
            response.put("message", affectedRows + "개의 정보가 갱신되었습니다.");
            return response;
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }

    // 여러 좌석 정보 삭제
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> deleteSeatPriceArray(Map<String, Object> params) {
        try {
            int affectedRows = seatPriceMapper.deleteSeatPriceArray(params);
            Map<String, Object> response = new HashMap<>();
            response.put("message", affectedRows + "개의 정보가 삭제되었습니다.");
            return response;
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }
}
