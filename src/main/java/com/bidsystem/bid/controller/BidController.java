package com.bidsystem.bid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import com.bidsystem.bid.service.BidService;

@RestController
@RequestMapping("/api/bid")
public class BidController {

    @Autowired
    private BidService bidService;

    // 좌석 입찰 정보를 가져오기
    @PostMapping("/get-by-seatarray")
    public ResponseEntity<List<Map<String, Object>>> getBidsBySeatArray(@RequestBody Map<String, Object> params) {
        List<Map<String, Object>> results = bidService.getBidsBySeatArray(params); 
        return ResponseEntity.ok(results); 
    }

    // 사용자의 모든 입찰 조회
    @GetMapping("/get-mybids")
    public ResponseEntity<List<Map<String, Object>>> getMyBids(@RequestParam Map<String, Object> params) {
        List<Map<String, Object>> results = bidService.getMyBids(params); 
        return ResponseEntity.ok(results); 
    }

    @GetMapping("/get-mylastbids")
    public ResponseEntity<List<Map<String, Object>>> getMyLastBids(@RequestParam Map<String, Object> params) {
        List<Map<String, Object>> results = bidService.getMyLastBids(params); 
        return ResponseEntity.ok(results); 
    }

    
    // 사용자의 모든 입찰 조회
    @GetMapping("/get-bid-tallies")
    public ResponseEntity<List<Map<String, Object>>> getBidTallies(@RequestParam Map<String, Object> params) {
        List<Map<String, Object>> results = bidService.getBidTallies(params); 
        return ResponseEntity.ok(results); 
    }
    // 사용자의 모든 입찰 조회
    @GetMapping("/get-highest-bids")
    public ResponseEntity<List<Map<String, Object>>>  getHighestBids(@RequestParam Map<String, Object> params) {
        List<Map<String, Object>> results = bidService.getHighestBids(params); 
        return ResponseEntity.ok(results); 
    }

    // 사용자의 모든 입찰 조회
    @GetMapping("/get-all-bids")
    public ResponseEntity<List<Map<String, Object>>> getAllBids(@RequestParam Map<String, Object> params) {
        List<Map<String, Object>> results = bidService.getAllBids(params); 
        return ResponseEntity.ok(results); 
    }

    // 낙찰 처리
    @PostMapping("/award")
    public ResponseEntity<Map<String, Object>> awardBids(@RequestBody Map<String, Object> request) {
        Map<String, Object> results = bidService.awardBids(request); 
        return ResponseEntity.ok(results); 
    }

    // 입찰 제출
    @PostMapping("/submit")
    public ResponseEntity<List<Map<String, Object>>> submitBids(@RequestBody Map<String, Object> request) {
        List<Map<String, Object>> results = bidService.submitBids(request); 
        return ResponseEntity.ok(results); 
    }

}
