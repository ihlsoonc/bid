package com.bidsystem.bid.service;

import com.bidsystem.bid.mapper.MatchMapper;

import com.bidsystem.bid.service.ExceptionService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.rmi.ServerException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
public class MatchService {

    @Autowired
    private MatchMapper matchMapper;

    // 경기 비드 상태 조회
    public Map<String, Object> getBidStatus(Map<String, Object> params) {
        try {
            Map<String, Object> results = matchMapper.getBidStatus(params); 
            
            // if (results == null || results.isEmpty()) {
            //     throw new NotFoundException(null);
            // } else {
                String bidStatusCode = "I"; // 기본값: 데이터가 없을 경우

                // 'bid_open_datetime'과 'bid_close_datetime'을 LocalDateTime으로 캐스팅
                LocalDateTime openDateTime = (LocalDateTime) results.get("bid_open_datetime");
                LocalDateTime closeDateTime = (LocalDateTime) results.get("bid_close_datetime");


                // 현재 시간을 LocalDateTime으로 가져옴
                LocalDateTime now = LocalDateTime.now();

                // bid_open_status가 'F'이면 F로 설정, 입찰 개시/종료 시간으로 결정
                String bidOpenStatus = (String) results.get("bid_open_status");
                if ("F".equals(bidOpenStatus)) {
                    bidStatusCode = "F"; // 'F' 상태
                } else {
                    // 현재 시간과 비교하여 상태 코드 설정
                    if (now.isBefore(openDateTime)) {
                        bidStatusCode = "N"; // 입찰 시작 전
                    } else if (now.isAfter(closeDateTime)) {
                        bidStatusCode = "C"; // 입찰 종료 후
                    } else {
                        bidStatusCode = "O"; // 입찰 진행 중
                    }
                }
                Map<String, Object> response = new HashMap<>(results);
                response.put("bidStatusCode", bidStatusCode);
                return response;
            // }
        } catch (Exception e) {
            throw new DataAccessException( null,e);
        }
    }
    public String getStatus(Map<String, Object> results) {
            
        String bidStatusCode = "I"; // 기본값: 데이터가 없을 경우

        // 'bid_open_datetime'과 'bid_close_datetime'을 LocalDateTime으로 캐스팅
        LocalDateTime openDateTime = (LocalDateTime) results.get("bid_open_datetime");
        LocalDateTime closeDateTime = (LocalDateTime) results.get("bid_close_datetime");

        // 현재 시간을 LocalDateTime으로 가져옴
        LocalDateTime now = LocalDateTime.now();

        // bid_open_status가 'F'이면 F로 설정, 입찰 개시/종료 시간으로 결정
        String bidOpenStatus = (String) results.get("bid_open_status");
        if ("F".equals(bidOpenStatus)) {
            bidStatusCode = "F"; // 'F' 상태
        } else {
            // 현재 시간과 비교하여 상태 코드 설정
            if (now.isBefore(openDateTime)) {
                bidStatusCode = "N"; // 입찰 시작 전
            } else if (now.isAfter(closeDateTime)) {
                bidStatusCode = "C"; // 입찰 종료 후
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

    // 모든 경기 조회
    public List<Map<String, Object>> getAllMatches(Map<String, Object> params) {
        String userType = (String) params.get("userType");

        try {
            if ("B".equals(userType)) {
                return matchMapper.getMatchesByUserId(params);
            } else {
                return matchMapper.getAllMatches(params);
            }
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }

    // 모든 승인된 경기 조회 (사용자용)
    public List<Map<String, Object>> getAllApprovedMatches(Map<String, Object> params) {
        try {
            return matchMapper.getAllMatches(params);
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }

    // 경기 추가
    public Map<String, Object> addMatch(Map<String, Object> params) {
        try {
            int affectedRows = matchMapper.addMatch(params);
            if (affectedRows > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "성공적으로 경기정보가 생성되었습니다.");
                return response;
            } else {
                throw new NotFoundException(null);
            }
        } catch (DuplicateKeyException e) {
            // 중복 키 예외 처리
            throw new DuplicateKeyException(null);
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }

    // 경기 수정
    public Map<String, Object> updateMatch(Map<String, Object> params) {
        try {
            int affectedRows = matchMapper.updateMatch(params);
            if (affectedRows > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "갱신 처리가 성공적으로 수행되었습니다.");
                return response;
            } else {
                throw new NotFoundException(null);
            }
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
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }
}
