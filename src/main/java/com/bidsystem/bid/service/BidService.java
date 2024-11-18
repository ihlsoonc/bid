package com.bidsystem.bid.service;

import com.bidsystem.bid.mapper.BidMapper;
import com.bidsystem.bid.mapper.MatchMapper;
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
    private static final Logger logger = LoggerFactory.getLogger(BidService.class); 
    @Autowired
    private BidMapper bidMapper;
    

    @Autowired
    private MatchMapper matchMapper;

    public List<Map<String, Object>> getBidsBySeatArray(Map<String, Object> params) {
        try {
            List<Map<String, Object>> results =  bidMapper.getBidsBySeatArray(params); 
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
    
    
    public List<Map<String, Object>> getMyBids(Map<String, Object> params) {
        try {
            List<Map<String, Object>> results =  bidMapper.getMyBids(params); 
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
    
    public List<Map<String, Object>> getBidTallies(Map<String, Object> params) {
        try {
            List<Map<String, Object>> results =  bidMapper.getBidTallies(params); 
            if (results == null || results.isEmpty()) {
                throw new NoDataException("입찰 내역이 없습니다.");
            } else {
                return results;
            }
        } catch (NoDataException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }
    
    public List<Map<String, Object>> getAllBids(Map<String, Object> params) {
        try {
            List<Map<String, Object>> results =  bidMapper.getAllBids(params); 
            if (results == null || results.isEmpty()) {
                throw new NoDataException("입찰 내역이 없습니다.");
            } else {
                return results;
            }
        } catch (NoDataException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }

    public List<Map<String, Object>> getHighestBids(Map<String, Object> params) {
        try {
            List<Map<String, Object>>  results =  bidMapper.getHighestBids(params); 
            if (results == null || results.isEmpty()) {
                throw new NoDataException("입찰 내역이 없습니다.");
            } else {
                return results;
            }
        } catch (NoDataException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }

    public List<Map<String, Object>> getMyLastBids(Map<String, Object> params) {
        try {
            List<Map<String, Object>> results =  bidMapper.getMyLastBids(params); 
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

    @Transactional
    public Map<String, Object> updateBidPayment(Map<String, Object> params) {

        String telno = (String) params.get("telno");
        if (telno == null) {
            throw new java.lang.IllegalArgumentException("telno가 null입니다.");
        }

        // 입찰 금액 추출 및 검증
        Integer bidAmount = null;
        if (params.get("bidAmount") instanceof Integer) {
            bidAmount = (Integer) params.get("bidAmount");
        } else if (params.get("bidAmount") instanceof String) {
            try {
                bidAmount = Integer.parseInt((String) params.get("bidAmount"));
            } catch (NumberFormatException e) {
                throw new java.lang.IllegalArgumentException("오류 : bidAmount값 무효.");
            }
        }
        if (bidAmount == null) {
            throw new java.lang.IllegalArgumentException("오류 : BidAmount 값이 null입니다.");
        }

        // 거래 ID 추출 및 검증
        String tid = (String) params.get("tid");
        if (tid == null) {
            throw new java.lang.IllegalArgumentException("시스템 오류 : tid값이 null입니다.");
        }

        // 결제 방법 추출 및 검증
        String payMethod = (String) params.get("payMethod");
        if (payMethod == null) {
            throw new java.lang.IllegalArgumentException("시스템 오류 : payMethod값이 null입니다.");
        }
  
        try {
            Map<String, Object> updateParams = Map.of(
                "telno", telno,
                "bidAmount", bidAmount,
                "tid", tid,
                "payMethod", payMethod
            );
            
            // 낙찰된 건에 대해 지불방법과 거래 ID 갱신 
            int rowsaffected = bidMapper.updatePayment(updateParams);
            if (rowsaffected  == 0) {
                throw new NotFoundException("Error in finding bids data for updating payment infomation");
            }
            else {
                Map<String, Object> response = new HashMap<>();
                response.put("message", rowsaffected+"건의 지불내용이 성공적으로 수정되었습니다.");  
                return response;
            }
        } catch (java.lang.IllegalArgumentException | NotFoundException e) {
            throw e;
        
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Map<String, Object>> submitBids(Map<String, Object> params) {
        List<Map<String, Object>> bidArray = (List<Map<String, Object>>) params.get("bidArray");
        String telno = (String) params.get("telno");
        String matchNumber = (String) params.get("matchNumber");

        List<Map<String, Object>> resultsArray = new ArrayList<>();

        if (bidArray == null || !(bidArray instanceof List)) {
            throw new BadRequestException(null);
        }
        
        //입찰된 좌석건별로 데이터 포멧 체므 반복 수행
        int bidAmount = 0;
        for (Map<String, Object> bid : bidArray) {
            Object seatNoObj = bid.get("seatNo");
            String seatNo ="";
            if (seatNoObj instanceof String) {
                    seatNo = (String) seatNoObj; 
            } else if (seatNoObj instanceof Integer) {
                seatNo = Integer.toString((int) seatNoObj);
            } 

            Object bidAmountObj = bid.get("bidAmount");
            if (bidAmountObj instanceof String) {
                bidAmount = Integer.parseInt((String) bidAmountObj);
            } else if (bidAmountObj instanceof Integer) {
                bidAmount = (Integer) bidAmountObj;
            } else if (bidAmountObj instanceof Double) {
                bidAmount = ((Double) bidAmountObj).intValue();
            } 

            //입찰된 좌석건별로 반복 수행
            Map<String, Object> getparams = new HashMap<>();
            getparams.put("matchNumber", matchNumber);
            getparams.put("seatNo", seatNo);
            try {
                //해당 seatNo의 현재 시점의 최고 입찰금액을 조회
                Map<String, Object> results = bidMapper.getMaxBidAmount(getparams);
                int maxBidAmount =0;
                if (results != null) {
                    Object maxBidAmountObject = results.get("max_bid_amount");
                    if (maxBidAmountObject instanceof String) {
                        try {
                            maxBidAmount = Integer.parseInt((String) maxBidAmountObject);
                        } catch (NumberFormatException e) {
                            throw new NumberFormatException();
                        }
                    } else if (maxBidAmountObject instanceof Integer) {
                        maxBidAmount = (Integer) maxBidAmountObject;
                    } else if (bidAmountObj instanceof Double) {
                        maxBidAmount = ((Double) maxBidAmountObject).intValue();
                    } else {
                        throw new NumberFormatException();
                    }
                }
                
                //해당 seatNo의 현재 시점의 최고 입찰금액보다 금액이 크지 않으면 등록 실패, 아니면 성공으로 반환
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
                bidParams.put("telno", telno);
                bidParams.put("matchNumber", matchNumber);
                bidParams.put("seatNo", seatNo);
                bidParams.put("bidAmount", bidAmount);

                bidMapper.submitBid(bidParams);

                Map<String, Object> resultEach = new HashMap<>();
                resultEach.put("status", "success");
                resultEach.put("seat_no", seatNo);
                resultEach.put("message", "등록 성공");
                resultsArray.add(resultEach);

            } catch (NumberFormatException | BadRequestException e) {
                throw e;
            } catch (Exception e) {
                throw new DataAccessException(null,e);
            }
        }

        return resultsArray;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> awardBids(Map<String, Object> params) {
        try {

            int affectedRowsStatus = matchMapper.updateMatchAwardStatus(params);  // updateBidStatus 호출 (낙찰 처리 flag =F' set)
            if (affectedRowsStatus == 0) {
                throw new NotFoundException(null);
            }
            int affectedRowsBids = bidMapper.awardBids(params);  // awardBids 호출

            if (affectedRowsBids > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", affectedRowsBids+"건이 성공적으로 낙찰 처리되었습니다.");
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
