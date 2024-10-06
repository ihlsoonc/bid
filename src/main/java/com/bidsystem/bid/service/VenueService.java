package com.bidsystem.bid.service;

import com.bidsystem.bid.service.ExceptionService.*;
import com.bidsystem.bid.mapper.VenueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Service
public class VenueService {

    @Autowired
    private VenueMapper venueMapper;


    // 특정 경기장 조회
    public Map<String, Object> getVenueByCode(Map<String, Object> params) {
        try {
            Map<String, Object> results = venueMapper.getVenueByCode(params);
            if (results == null || results.isEmpty()) {
                throw new NotFoundException(null);
            }
            return results;
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }

    // 모든 경기장 조회
    public List<Map<String, Object>> getAllVenues(Map<String, Object> params) {
        try {
            List<Map<String, Object>> results = venueMapper.getAllVenues();
            if (results == null || results.isEmpty()) {
                throw new NotFoundException(null);
            }
            return results;
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }
    // 경기장 추가
    public Map<String, Object> addVenue(Map<String, Object> params) {
        try {
            int affectedRows =  venueMapper.addVenue(params);
            if (affectedRows > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "삭제 처리가 성공적으로 수행되었습니다.");
                return response;
            } else {
                throw new NotFoundException(null);
            }
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }

    // 경기장 수정
    public Map<String, Object> updateVenue(Map<String, Object> params) {
        try {
            int affectedRows =  venueMapper.updateVenue(params);
            if (affectedRows > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "삭제 처리가 성공적으로 수행되었습니다.");
                return response;
            } else {
                throw new NotFoundException(null);
            }
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }

    // 경기장 삭제
    public Map<String, Object> deleteVenue(Map<String, Object> params){
        try {
            int affectedRows =  venueMapper.deleteVenue(params);
            if (affectedRows > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "삭제 처리가 성공적으로 수행되었습니다.");
                return response;
            } else {
                throw new NotFoundException(null);
            }
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }
}
