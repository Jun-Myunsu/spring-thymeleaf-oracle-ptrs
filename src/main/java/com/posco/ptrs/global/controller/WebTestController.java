// src/main/java/com/posco/ptrs/global/controller/WebTestController.java
package com.posco.ptrs.global.controller;

import com.posco.ptrs.global.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/test")
public class WebTestController {
    
    @GetMapping("/error")
    public String testError() {
        log.info("웹 페이지 오류 테스트");
        throw new RuntimeException("웹 페이지 오류 테스트입니다");
    }
    
    @GetMapping("/not-found")
    public String testNotFound() {
        log.info("404 오류 테스트");
        throw new UserNotFoundException("페이지를 찾을 수 없습니다");
    }
}