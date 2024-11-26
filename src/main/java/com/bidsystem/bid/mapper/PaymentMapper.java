package com.bidsystem.bid.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.Map;

@Mapper
public interface PaymentMapper {
        void saveMobileRequest(Map<String, Object> paymentData);
    
        void saveMobileApproval(Map<String, Object> paymentData);
    
        void savePcRequest(Map<String, Object> paymentData);
    
        void savePCApproval(Map<String, Object> paymentData);
}
