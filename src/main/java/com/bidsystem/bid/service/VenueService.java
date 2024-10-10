package com.bidsystem.bid.service;

import com.bidsystem.bid.service.ExceptionService.*;
import com.bidsystem.bid.mapper.VenueMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Service
public class VenueService {

    @Autowired
    private VenueMapper venueMapper;
    private static final Logger logger = LoggerFactory.getLogger(BidService.class);


    // 특정 경기장 조회
    public Map<String, Object> getVenueByCode(Map<String, Object> params) {
        try {
            Map<String, Object> results = venueMapper.getVenueByCode(params);
            if (results == null || results.isEmpty()) {
                throw new NotFoundException(null);
            }
            return results;
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }

    // 모든 경기장 조회
    public List<Map<String, Object>> getAllVenues(Map<String, Object> params) {
        try {
            List<Map<String, Object>> results = venueMapper.getAllVenues();
            if (results == null || results.isEmpty()) {
                throw new NoDataException(null);
            }
            return results;
        } catch (NoDataException e) {
            throw e;
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
                response.put("message", "성공적으로 등록되었습니다.");
                return response;
            } else {
                throw new ZeroAffectedRowException(null);
            }
        } catch (ZeroAffectedRowException e) {
            throw e;

            //DUPKEY를 catch하기 위함 이렇게 하지 않으면 DataAccessException으로 catch됨
        } catch (org.springframework.dao.DataAccessException e) { 
            if (e instanceof org.springframework.dao.DuplicateKeyException) {       
                throw new DuplicateKeyException("중복된 정보입니다. 입력 내용을 확인하세요.");
            } else {
                throw new DataAccessException(null,e);
            }
        }
    }

    // 경기장 수정
    public Map<String, Object> updateVenue(Map<String, Object> params) {
        try {
            int affectedRows =  venueMapper.updateVenue(params);
            if (affectedRows > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "정보가 성공적으로 수정되었습니다.");
                return response;
            } else {
                throw new NotFoundException(null);
            }
        } catch (NotFoundException e) {
            throw e;
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
                response.put("message", "성공적으로 삭제되었습니다.");
                return response;
            } else {
                throw new NotFoundException(null);
            }
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }
}
