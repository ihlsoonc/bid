package com.bidsystem.bid.controller;

import com.bidsystem.bid.service.PgService;
import com.bidsystem.bid.service.PgServiceMobile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequestMapping("/api")

public class PgController {

    @Autowired
    private PgService pgService;

    @Autowired
    private PgServiceMobile pgServiceMobile;

    @GetMapping("/pgstart")
    public ModelAndView pgStartGet(@RequestParam Map<String, Object> request) {
        ModelAndView pgstart = pgService.pgStart(request);
        return pgstart;
    }
    @GetMapping("/pgstart-mobile")
    public ModelAndView pgStartGetMobile(@RequestParam Map<String, Object> request) {
        ModelAndView pgstart = pgServiceMobile.pgStartMobile(request);
        return pgstart;
    }

    @PostMapping("/pgreturn")
    public ModelAndView pgReturnPost(@RequestBody String request) {
        ModelAndView pgreturn = pgService.pgReturn(request);
        return pgreturn;
    }

    @PostMapping("/pgreturn-mobile")
    public ModelAndView pgReturnPostMobile(@RequestBody String request) {
        ModelAndView pgreturn = pgServiceMobile.pgReturnMobile(request);
        return pgreturn;
    }
    @GetMapping("/pgclose")
    public String closePage() throws InterruptedException {
        return "close"; //close.jsp
    }
}

