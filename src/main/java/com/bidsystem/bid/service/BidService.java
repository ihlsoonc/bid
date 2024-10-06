package com.bidsystem.bid.service;

import com.bidsystem.bid.mapper.BidMapper;
import com.bidsystem.bid.mapper.MatchMapper;
import com.bidsystem.bid.mapper.UserMapper;
import com.bidsystem.bid.service.ExceptionService.*;

import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class BidService {
    private static final Logger logger = LoggerFactory.getLogger(BidService.class); // 로거 생성
    @Autowired
    private BidMapper bidMapper;
    
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MatchMapper matchMapper;

    public List<Map<String, Object>> getBidsBySeatArray(Map<String, Object> params) {
        try {
            return bidMapper.getBidsBySeatArray(params); // 실제 데이터 가져오기
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }
    
    public List<Map<String, Object>> getMyBids(Map<String, Object> params) {
        try {
            return bidMapper.getMyBids(params); // 실제 데이터 가져오기
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }
    
    public List<Map<String, Object>> getMyLastBids(Map<String, Object> params) {
        try {
            return bidMapper.getMyLastBids(params); // 실제 데이터 가져오기
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }

    @Transactional
    public Map<String, Object> updateBidPayment(Map<String, Object> params) {
        // 전화번호 추출 및 검증
        String userId = (String) params.get("userId");
        if (userId == null) {
            throw new IllegalArgumentException("userId가 null입니다.");
        }

        // 입찰 금액 추출 및 검증 (Object에서 Integer로 안전하게 변환)
        Integer bidAmount = null;
        if (params.get("bidAmount") instanceof Integer) {
            bidAmount = (Integer) params.get("bidAmount");
        } else if (params.get("bidAmount") instanceof String) {
            try {
                bidAmount = Integer.parseInt((String) params.get("bidAmount"));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("bidAmount 값이 유효한 숫자가 아닙니다.");
            }
        }
        if (bidAmount == null) {
            throw new IllegalArgumentException("입찰 금액(bidAmount)이 null이거나 유효하지 않습니다.");
        }

        // 거래 ID 추출 및 검증
        String tid = (String) params.get("tid");
        if (tid == null) {
            throw new IllegalArgumentException("거래 ID(tid)가 null입니다.");
        }

        // 결제 방법 추출 및 검증
        String payMethod = (String) params.get("payMethod");
        if (payMethod == null) {
            throw new IllegalArgumentException("결제 방법(payMethod)이 null입니다.");
        }
  
        try {
            Map<String, Object> updateParams = Map.of(
                "userId", userId,
                "bidAmount", bidAmount,
                "tid", tid,
                "payMethod", payMethod
            );

            try {
                int rowsaffected = bidMapper.updatePayment(updateParams);
                if (rowsaffected  == 0) {
                    throw new NotFoundException("결제정보를 갱신하기 위한 입찰 데이터를 찾을 수 없습니다.");
                }
                Map<String, Object> response = new HashMap<>();
                response.put("message", rowsaffected+"건의 지불내용이 성공적으로 수정되었습니다.");  
                return response;
            }
            catch (Exception e) {
                throw new DataAccessException(null,e);
            }
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Map<String, Object>> submitBids(Map<String, Object> params) {
        List<Map<String, Object>> bidArray = (List<Map<String, Object>>) params.get("bidArray");
        String userId = (String) params.get("userId");
        String matchNumber = (String) params.get("matchNumber");

        List<Map<String, Object>> resultsArray = new ArrayList<>();

        if (bidArray == null || !(bidArray instanceof List)) {
            throw new BadRequestException(null);
        }
        int bidAmount = 0;
        for (Map<String, Object> bid : bidArray) {
            Object seatNoObj = bid.get("seatNo");
            String seatNo ="";
            if (seatNoObj instanceof String) {
                try {
                    System.out.println("\n\nBid seatNoObj is of type String: " + seatNoObj);
                    seatNo = (String) seatNoObj;
                } catch (NumberFormatException e) {
                    System.out.println("\n\nFailed to convert String to Integer");
                    throw new NumberFormatException();
                }
            } else if (seatNoObj instanceof Integer) {
                System.out.println("\n\nBid seatNoObj is of type Integer: " + seatNoObj);
                seatNo = Integer.toString((int) seatNoObj);
            } else {
                System.out.println("\n\nUnsupported type for bid seatNoObj: " + seatNoObj.getClass().getSimpleName());
                throw new NumberFormatException();
            }
            Object bidAmountObj = bid.get("bidAmount");
            if (bidAmountObj instanceof String) {
                try {
                    System.out.println("\n\nBid amount is of type String: " + bidAmountObj);
                    bidAmount = Integer.parseInt((String) bidAmountObj);
                } catch (NumberFormatException e) {
                    System.out.println("\n\nFailed to convert String to Integer");
                    throw new NumberFormatException();
                }
            } else if (bidAmountObj instanceof Integer) {
                System.out.println("\n\nBid amount is of type Integer: " + bidAmountObj);
                bidAmount = (Integer) bidAmountObj;
            } else if (bidAmountObj instanceof Double) {
                System.out.println("\n\nBid amount is of type Double: " + bidAmountObj);
                bidAmount = ((Double) bidAmountObj).intValue();
            } else {
                System.out.println("\n\nUnsupported type for bid amount: " + bidAmountObj.getClass().getSimpleName());
                throw new NumberFormatException();
            }
            
            Map<String, Object> getparams = new HashMap<>();
            getparams.put("matchNumber", matchNumber);
            getparams.put("seatNo", seatNo);
            try {
                Map<String, Object> results = bidMapper.getMaxBidAmount(getparams);
                logger.info("\n\n===========================max amout", results);
                int maxBidAmount =0;
                if (results != null) {
                    Object maxBidAmountObject = results.get("max_bid_amount");
                    if (maxBidAmountObject instanceof String) {
                        try {
                            System.out.println("\n\nBid maxBidAmountObject is of type String: " + maxBidAmountObject);
                            maxBidAmount = Integer.parseInt((String) maxBidAmountObject);
                        } catch (NumberFormatException e) {
                            System.out.println("\n\nFailed to convert String to Integer");
                            throw new NumberFormatException();
                        }
                    } else if (maxBidAmountObject instanceof Integer) {
                        System.out.println("\n\nBid maxBidAmountObject is of type Integer: " + maxBidAmountObject);
                        maxBidAmount = (Integer) maxBidAmountObject;
                    } else if (bidAmountObj instanceof Double) {
                        System.out.println("\n\nBid maxBidAmountObject is of type Double: " + maxBidAmountObject);
                        maxBidAmount = ((Double) maxBidAmountObject).intValue();
                    } else {
                        System.out.println("\n\nUnsupported type for maxBidAmountObject amount: " + maxBidAmountObject.getClass().getSimpleName());
                        throw new NumberFormatException();
                    }
    
                }
                
                if (bidAmount <= maxBidAmount) {
                    Map<String, Object> resultEach = new HashMap<>();
                    resultEach.put("status", "fail");
                    resultEach.put("seat_no", seatNo);
                    resultEach.put("message", "등록 실패: 입찰액이 현재 최대 입찰액보다 작습니다.");
                    resultsArray.add(resultEach);
                    continue;
                }

                Map<String, Object> bidParams = new HashMap<>();
                bidParams.put("bidAt", new Timestamp(System.currentTimeMillis()));
                bidParams.put("userId", userId);
                bidParams.put("matchNumber", matchNumber);
                bidParams.put("seatNo", seatNo);
                bidParams.put("bidAmount", bidAmount);

                bidMapper.submitBid(bidParams);

                Map<String, Object> resultEach = new HashMap<>();
                resultEach.put("status", "success");
                resultEach.put("seat_no", seatNo);
                resultEach.put("message", "등록 성공");
                resultsArray.add(resultEach);
            } catch (Exception e) {
                throw new DataAccessException(null,e);
            }
        }

        return resultsArray;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> awardBids(Map<String, Object> params) {
        try {
            int affectedRows = matchMapper.updateMatchAwardStatus(params);  // updateBidStatus 호출 (낙찰 처리 flag =F' set)
            if (affectedRows == 0) {
                throw new NotFoundException(null);
            }
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
        try { 
            int result = bidMapper.awardBids(params);  // awardBids 호출
            Map<String, Object> response = new HashMap<>();
            response.put("message", result+"건이 성공적으로 낙찰 처리되었습니다.");
            return response;
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }
}
