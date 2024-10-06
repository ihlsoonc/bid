package com.bidsystem.bid.controller;

import com.bidsystem.bid.service.SeatPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/seatprice")
public class SeatPriceController {

    @Autowired
    private SeatPriceService seatPriceService;

    // 특정 경기의 좌석 가격을 조회
    @GetMapping("/getbyid")
    public ResponseEntity<List<Map<String, Object>>> getSeatPrices(@RequestParam Map<String, Object> params) {
        List<Map<String, Object>> seatPrices = seatPriceService.getSeatPrices(params); // 서비스에서 데이터 반환
        return ResponseEntity.ok(seatPrices); // 데이터를 ResponseEntity로 감싸서 반환
    }

    // 좌석 가격을 배열로 업데이트
    @PostMapping("/updatearray")
    public ResponseEntity<String> updateSeatPriceArray(@RequestBody Map<String, Object> params) {
        seatPriceService.updateSeatPriceArray(params); // 서비스에서 배열 업데이트 처리
        return ResponseEntity.ok("좌석 가격 배열이 성공적으로 업데이트되었습니다."); // 성공 메시지 반환
    }

    // 좌석 가격 배열 삭제
    @PostMapping("/deletearray")
    public ResponseEntity<String> deleteSeatPriceArray(@RequestBody Map<String, Object> params) {
        seatPriceService.deleteSeatPriceArray(params); // 서비스에서 배열 삭제 처리
        return ResponseEntity.ok("좌석 가격 배열이 성공적으로 삭제되었습니다."); // 성공 메시지 반환
    }
}
