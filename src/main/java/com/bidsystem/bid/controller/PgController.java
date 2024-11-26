package com.bidsystem.bid.controller;

import com.bidsystem.bid.service.ExceptionService.PgException;
import com.bidsystem.bid.service.PgCommon;
import com.bidsystem.bid.service.PgCommon.PgParams;
import com.bidsystem.bid.service.PgCommon.Urls;
import com.bidsystem.bid.service.PgCommon.Views;
import com.bidsystem.bid.service.PgService;
import com.bidsystem.bid.service.PgServiceMobile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api")
public class PgController {
@Autowired
    private PgService pgService;
@Autowired
    private PgServiceMobile pgServiceMobile;

    //결제 요청 : PC
    @GetMapping("/pgstart")
    public ModelAndView pgStartGet(@RequestParam Map<String, Object> request) {
        ModelAndView pgstart = pgService.pgStart(request);
        return pgstart;
    }

    //결제 요청에 대한 응답 및 승인 요청 : PC
    @GetMapping("/pgstart-mobile")
    public ModelAndView pgStartGetMobile(@RequestParam Map<String, Object> request) {
        ModelAndView pgstart = pgServiceMobile.pgStartMobile(request);
        return pgstart;
    }

    
    //결제 요청 : mobile
    @PostMapping("/pgreturn")
    public ModelAndView pgReturnPost(@RequestBody String request) {
        ModelAndView pgreturn = pgService.pgReturn(request);
        return pgreturn;
    }

    //결제 요청에 대한 응답 및 승인 요청 : mobile
    @PostMapping("/pgreturn-mobile")
    public ModelAndView pgReturnPostMobile(@RequestBody String request) {
        ModelAndView pgreturn = pgServiceMobile.pgReturnMobile(request);
        return pgreturn;
    }

    @GetMapping("/pgclose")
    public ModelAndView closePage() {
        ModelAndView pgclose = new ModelAndView();
        pgclose.setViewName(Views.CLOSE);
        return pgclose;
    }
    
        
}

