package com.bidsystem.bid.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.Map;

@Mapper
public interface AdminMapper {

    // 사용자 정보 조회 (ID 또는 전화번호로)
    Map<String, Object> getUserByQuery(Map<String, Object> request);

    // 사용자 정보 조회 (ID)
    Map<String, Object> getUserById(Map<String, Object> request);
    
    // 사용자 ID 조회 (이메일과 이름으로)
    Map<String, Object> getUserIdByEmailAndName(Map<String, Object> request);

    // 사용자 등록
    int registerUser(Map<String, Object> request);

    // 사용자 정보 수정
    int updateUser(Map<String, Object> request);

    // 비밀번호 변경
    int changePassword(Map<String, Object> request);
}
