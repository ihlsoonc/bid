package com.bidsystem.bid.controller;

import com.bidsystem.bid.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/match")
public class MatchController {

    @Autowired
    private MatchService matchService;

    // 입찰 상태 조회
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getBidStatus(@RequestParam Map<String, Object> params) {
        Map<String, Object> results = matchService.getBidStatus(params); 
        return ResponseEntity.ok(results); 
    }

    // 특정 경기 조회
    @GetMapping("/getbyid")
    public ResponseEntity<Map<String, Object>> getMatchById(@RequestParam Map<String, Object> params) {
        Map<String, Object> results = matchService.getMatchById(params); 
        return ResponseEntity.ok(results); 
    }

    // 모든 경기 조회
    @GetMapping("/getall")
    public ResponseEntity<List<Map<String, Object>>> getAllMatches(@RequestParam Map<String, Object> params) {
        List<Map<String, Object>> results = matchService.getAllMatches(params); 
        return ResponseEntity.ok(results); 
    }

    // 승인된 경기 조회
    @GetMapping("/getallapproved")
    public ResponseEntity<List<Map<String, Object>>> getAllApprovedMatches(@RequestParam Map<String, Object> params) {
        List<Map<String, Object>> results = matchService.getAllApprovedMatches(params); 
        return ResponseEntity.ok(results); 
    }

    // 경기 추가
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addMatch(@RequestBody Map<String, Object> request) {
        Map<String, Object> results  = matchService.addMatch(request); 
        return ResponseEntity.ok(results); 
    }

    // 경기 수정
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateMatch(@RequestBody Map<String, Object> request) {
        Map<String, Object> results  = matchService.updateMatch(request); 
        return ResponseEntity.ok(results); 
    }

    // 경기 승인
    @PostMapping("/approve")
    public ResponseEntity<Map<String, Object>> approveMatch(@RequestBody Map<String, Object> request) {
        Map<String, Object> results  = matchService.approveMatch(request); 
        return ResponseEntity.ok(results); 
    }

    // 경기 삭제
    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteMatch(@RequestBody Map<String, Object> request) {
        Map<String, Object> results  = matchService.deleteMatch(request); 
        return ResponseEntity.ok(results); 
    }
}
