package com.bidsystem.bid.service;

import com.bidsystem.bid.mapper.MatchMapper;
import com.bidsystem.bid.service.ExceptionService.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class MatchService {

    @Autowired
    private MatchMapper matchMapper;
    private static final Logger logger = LoggerFactory.getLogger(BidService.class);

    // 경기 비드 상태 조회
    public Map<String, Object> getMatchBidStatus(Map<String, Object> params) {
        try {
            Map<String, Object> results = matchMapper.getMatchBidStatus(params); 

            if (results == null || results.isEmpty()) {
                throw new NotFoundException(null);
            } 
            String bidStatusCode = getBidStatusCode(results);
            Map<String, Object> response = new HashMap<>(results);
            response.put("bidStatusCode", bidStatusCode);
            return response;
        } catch (NoDataException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException( null,e);
        }
    }

    // 입찰개시 및 종료 일시로 입찰상태 코드 생성하는 함수
    public String getBidStatusCode(Map<String, Object> results) {
        String bidStatusCode = "I"; // 기본값: 데이터가 없을 경우
    
        // 'bid_open_datetime'과 'bid_close_datetime'을 Timestamp로 받아서 LocalDateTime으로 변환
        Timestamp openTimestamp = (Timestamp) results.get("bid_open_datetime");
        Timestamp closeTimestamp = (Timestamp) results.get("bid_close_datetime");
    
        LocalDateTime openDateTime = openTimestamp.toLocalDateTime();
        LocalDateTime closeDateTime = closeTimestamp.toLocalDateTime();
    
        // 현재 시간을 LocalDateTime으로 가져옴
        LocalDateTime now = LocalDateTime.now();
    
        // bid_open_status가 'F'이면 F로 설정, 입찰 개시/종료 시간으로 결정
        String bidOpenStatus = (String) results.get("bid_open_status");
        if ("F".equals(bidOpenStatus)) {
            bidStatusCode = "F"; // 낙찰완료 상태 
        } else {
            // 현재 시간과 비교하여 상태 코드 설정
            if (now.isBefore(openDateTime)) {
                bidStatusCode = "N"; // 입찰 시작 전
            } else if (now.isAfter(closeDateTime)) {
                bidStatusCode = "C"; // 입찰 종료 후 낙찰 처리 전
            } else {
                bidStatusCode = "O"; // 입찰 진행 중
            }
        }
    
        return bidStatusCode;
    }
    
    // 특정 경기 조회
    public Map<String, Object> getMatchById(Map<String, Object> params) {
        try {
            return matchMapper.getMatchById(params);
        } catch (Exception e) {
            throw new DataAccessException( null,e);
        }
    }

    // 모든 경기 조회 (venucd에 해당되는)
    public List<Map<String, Object>> getAllMatches(Map<String, Object> params) {
        try {
            List<Map<String, Object>> results = matchMapper.getAllMatches(params);
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

    // 본인이 등록한 경기 조회 (venucd에 해당되는)
    public List<Map<String, Object>> getMyMatches(Map<String, Object> params) {
        try {
            List<Map<String, Object>> results = matchMapper.getMyMatches(params);
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

    // 모든 승인된 경기 조회 (사용자용, venucd에 해당되는)
    public List<Map<String, Object>> getAllApprovedMatches(Map<String, Object> params) {
        try {
            List<Map<String, Object>> results = null;
            results =  matchMapper.getAllApprovedMatches(params);
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

    // 경기 추가
    @Transactional
    public Map<String, Object> addMatch(Map<String, Object> params) {
        try {
            // 첫 번째 쿼리로 최대 match_no 값을 조회
            Integer maxMatchNo = matchMapper.getMaxMatchNo();

            // 조회된 값에 1을 더해서 새로운 match_no 생성
            int newMatchNo = (maxMatchNo == null) ? 1 : maxMatchNo + 1;

            // 두 번째 쿼리로 새 데이터를 삽입
            params.put("matchNumber", newMatchNo);
            int affectedRows = matchMapper.addMatch(params);
            if (affectedRows > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "성공적으로 등록되었습니다.");
                response.put("newMatchNumber", newMatchNo);
                return response;
            } else {
                throw new ZeroAffectedRowException(null);
            }
        } catch (ZeroAffectedRowException e) {
            throw e;
        } catch (org.springframework.dao.DataAccessException e) {                  //DUPKEY를 catch하기 위함
            if (e instanceof org.springframework.dao.DuplicateKeyException) {       //DUPKEY를 catch하기 위함
                throw new DuplicateKeyException("중복된 정보입니다. 입력 내용을 확인하세요.");
            } else {
                throw new DataAccessException(null,e);
            }
        }
    }
    // 경기 수정
    public Map<String, Object> updateMatch(Map<String, Object> params) {
        try {
            int affectedRows = matchMapper.updateMatch(params);
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

    // 경기 승인
    public Map<String, Object> approveMatch(Map<String, Object> params) {
        try {
            int affectedRows = matchMapper.approveMatch(params); 
            if (affectedRows > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "승인 처리가 성공적으로 수행되었습니다.");
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

    // 경기 삭제
    public Map<String, Object> deleteMatch(Map<String, Object> params) {
        try {
            int affectedRows = matchMapper.deleteMatch(params); 
            if (affectedRows > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "삭제 처리가 성공적으로 수행되었습니다.");
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
