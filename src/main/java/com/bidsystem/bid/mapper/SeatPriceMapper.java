package com.bidsystem.bid.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;
import java.util.List;

@Mapper
public interface SeatPriceMapper {

    // 좌석별 가격 조회
    List< Map<String, Object>> getSeatPrice(Map<String, Object> params);

    // 좌석별 가격 입력 또는 갱신
    int updateSeatPrice(Map<String, Object> params);

    // 좌석배열 업데이트
    int updateSeatPriceArray(Map<String, Object> params);
    int deleteSeatPriceArray(Map<String, Object> params);
}
