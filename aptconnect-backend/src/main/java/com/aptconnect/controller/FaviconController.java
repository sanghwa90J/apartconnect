package com.aptconnect.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FaviconController {
    @RequestMapping("favicon.ico")
    @ResponseBody
    public void disableFavicon() { 
        // 브라우저에서 favicon 요청 시 아무 응답도 주지 않음
    }
}