package com.bidsystem.bid.controller;

import com.bidsystem.bid.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/venue")
public class VenueController {

    @Autowired
    private VenueService venueService;

    // 특정 경기장 조회
    @GetMapping("/getbycode")
    public ResponseEntity<Map<String, Object>> getVenueByCode(@RequestParam Map<String, Object> params) {
        Map<String, Object> result = venueService.getVenueByCode(params); 
        return ResponseEntity.ok(result); 
    }

    // 특정 경기장 조회
    @GetMapping("/getall")
    public ResponseEntity<List<Map<String, Object>>>getAllVenues(@RequestParam Map<String, Object> params) {
        List<Map<String, Object>> result = venueService.getAllVenues(params); 
        return ResponseEntity.ok(result); 
    }


    // 경기장 추가
    @PostMapping("/add")
    public ResponseEntity<String> addVenue(@RequestBody Map<String, Object> request) {
        venueService.addVenue(request); // 서비스 계층에서 추가 처리
        return ResponseEntity.ok("경기장이 성공적으로 추가되었습니다."); // 성공 메시지 반환
    }

    // 경기장 수정
    @PostMapping("/update")
    public ResponseEntity<String> updateVenue(@RequestBody Map<String, Object> request) {
        venueService.updateVenue(request); // 서비스 계층에서 수정 처리
        return ResponseEntity.ok("경기장이 성공적으로 수정되었습니다."); // 성공 메시지 반환
    }

    // 경기장 삭제
    @PostMapping("/delete")
    public ResponseEntity<String> deleteVenue(@RequestBody Map<String, Object> request) {
        venueService.deleteVenue(request); // 서비스 계층에서 삭제 처리
        return ResponseEntity.ok("경기장이 성공적으로 삭제되었습니다."); // 성공 메시지 반환
    }
}
