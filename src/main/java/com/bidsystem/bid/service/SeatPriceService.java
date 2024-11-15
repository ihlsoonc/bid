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
            List<Map<String, Object>> results = seatPriceMapper.getSeatPrice(params);
            if (results == null || results.isEmpty()) {
                throw new NoDataException(null);
            } else {
                return results;
            }
        } catch (NoDataException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }

    // 여러 좌석 정보 갱신
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updateSeatPriceArray(Map<String, Object> params) {
        try {
            int affectedRows = seatPriceMapper.updateSeatPriceArray(params);
            if (affectedRows > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", affectedRows + "개의 정보가 갱신되었습니다.");
                return response;
            } else {
                throw new ZeroAffectedRowException(null);
            }
        } catch (ZeroAffectedRowException e) {
            throw e;    
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }

    // 여러 좌석 정보 삭제
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> deleteSeatPriceArray(Map<String, Object> params) {
        try {
            int affectedRows = seatPriceMapper.deleteSeatPriceArray(params);
            if (affectedRows > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", affectedRows + "개의 정보가 삭제되었습니다.");
                return response;
            } else {
                throw new ZeroAffectedRowException(null);
            }
        } catch (ZeroAffectedRowException e) {
            throw e; 
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }
}
