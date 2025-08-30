// src/main/java/com/posco/ptrs/global/controller/TestController.java
package com.posco.ptrs.global.controller;

import com.posco.ptrs.global.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @GetMapping("/exception")
    public Map<String, String> testException() {
        log.info("일반 예외 테스트 시작");
        throw new RuntimeException("테스트용 런타임 예외입니다");
    }
    
    @GetMapping("/illegal-argument")
    public Map<String, String> testIllegalArgument() {
        log.info("잘못된 인수 예외 테스트 시작");
        throw new IllegalArgumentException("테스트용 잘못된 인수 예외입니다");
    }
    
    @GetMapping("/user-not-found")
    public Map<String, String> testUserNotFound() {
        log.info("사용자 없음 예외 테스트 시작");
        throw new UserNotFoundException("테스트용 사용자 없음 예외입니다");
    }
    
    @GetMapping("/db-error")
    public Map<String, String> testDbError() {
        log.info("DB 연결 오류 테스트 시작");
        throw new RuntimeException("Connection refused: localhost:5432");
    }
    
    @GetMapping("/timeout")
    public Map<String, String> testTimeout() {
        log.info("타임아웃 오류 테스트 시작");
        throw new RuntimeException("Query timeout after 30 seconds");
    }
    
    @GetMapping("/sql-error")
    public Map<String, String> testSqlError() {
        log.info("SQL 오류 테스트 시작");
        throw new RuntimeException("SQL syntax error near 'SELCT'");
    }
    
    @GetMapping("/conditional")
    public Map<String, String> testConditional(@RequestParam(required = false) String type) {
        log.info("조건부 예외 테스트 - type: {}", type);
        
        if (type == null) {
            throw new IllegalArgumentException("type 파라미터는 필수입니다");
        }
        
        return switch (type) {
            case "runtime" -> throw new RuntimeException("런타임 예외");
            case "user" -> throw new UserNotFoundException("사용자 ID: " + type);
            case "db" -> throw new RuntimeException("Database connection failed");
            default -> Map.of("message", "정상 응답", "type", type);
        };
    }
}