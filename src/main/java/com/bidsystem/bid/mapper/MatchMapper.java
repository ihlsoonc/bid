package com.bidsystem.bid.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

@Mapper
public interface MatchMapper {

    // 특정 경기 정보 가져오기
    Map<String, Object> getMatchById(Map<String, Object> params);
    
    // 비드 개시.종료 및 입찰 가능상태 조회
    Map<String, Object> getBidStatus(Map<String, Object> params);

    // 모든 경기 정보 가져오기
    List<Map<String, Object>> getAllMatches(Map<String, Object> params);

    // 사용자id로 생성한 경기 정보 가져오기
    List<Map<String, Object>> getMyMatches(Map<String, Object> params);

    // 승인된 경기 정보 가져오기
    List<Map<String, Object>> getAllApprovedMatches(Map<String, Object> params);

    // 경기 추가
    int addMatch(Map<String, Object> params);
    
    // 마지막 경기 번호
    int getMaxMatchNo();
    // 경기 수정
    int updateMatch(Map<String, Object> params);
    
    int updateMatchAwardStatus(Map<String, Object> params);
    int updateMatchAlimtalkStatus(Map<String, Object> params);
    // 경기 승인
    int approveMatch(Map<String, Object> params);

    // 경기 삭제
    int deleteMatch(Map<String, Object> params);
}
