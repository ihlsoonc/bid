package com.bidsystem.bid.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BidMapper {
    List<Map<String, Object>> getBidsBySeatArray(Map<String, Object> params);
    List<Map<String, Object>> getMyBids(Map<String, Object> params);
    List<Map<String, Object>> getMyLastBids(Map<String, Object> params);
    List<Map<String, Object>> getAllBids(Map<String, Object> params);
    
    // id로 경기별 낙찰내용과 금액 조회
    List<Map<String, Object>>  getMyAwardedBids(Map<String, Object> params);
    
    // 최대 입찰 금액 조회
    Map<String, Object> getMaxBidAmount(Map<String, Object> params);

    
    //입찰 제출
    int submitBid(Map<String, Object> params);

    //낙찰
    int awardBids(Map<String, Object> params);

    //지불후 입찰내역에 지불정보 기록
    int updatePayment(Map<String, Object> params);
    
    // 입찰 기록 삭제
    int deleteAllBids(Map<String, Object> params);
}
