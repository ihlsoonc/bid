package com.bidsystem.bid.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    private static final Logger logger = LoggerFactory.getLogger(SpaController.class);

    @Controller
    public class HomeController {

        private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

        @GetMapping("/{path:[^\\.]*}")
        public String home() {
            return "forward:/index.html"; // static/index.html로 포워딩
        }
    }
}
