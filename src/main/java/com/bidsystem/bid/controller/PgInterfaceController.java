package com.bidsystem.bid.controller;

import com.bidsystem.bid.service.PgInterfaceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequestMapping("/api")

public class PgInterfaceController {

    @Autowired
    private PgInterfaceService pgInterfaceService;

    @GetMapping("/pgstart")
    public ModelAndView pgStartGet(@RequestParam Map<String, Object> request) {
        ModelAndView pgstart = pgInterfaceService.pgStart(request);
        return pgstart;
    }

    @PostMapping("/pgreturnpost")
    public ModelAndView pgReturnPost(@RequestBody String request) {
        ModelAndView pgreturn = pgInterfaceService.pgReturn(request);
        return pgreturn;
    }
    @GetMapping("/pgclose")
    public String closePage() throws InterruptedException {
        return "close"; //close.jsp
    }
}

