// src/main/java/com/posco/ptrs/global/exception/GlobalExceptionHandler.java
package com.posco.ptrs.global.exception;

import com.posco.ptrs.global.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e, HttpServletRequest request) {
        log.error("예외 발생: {}", e.getMessage(), e);
        return createResponse(request, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e.getMessage(), "error/500");
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public Object handleIllegalArgument(IllegalArgumentException e, HttpServletRequest request) {
        log.warn("잘못된 요청: {}", e.getMessage());
        return createResponse(request, HttpStatus.BAD_REQUEST, "Bad Request", e.getMessage(), "error/error");
    }
    
    @ExceptionHandler(UserNotFoundException.class)
    public Object handleUserNotFound(UserNotFoundException e, HttpServletRequest request) {
        log.warn("사용자를 찾을 수 없음: {}", e.getMessage());
        return createResponse(request, HttpStatus.NOT_FOUND, "Not Found", e.getMessage(), "error/404");
    }
    
    private Object createResponse(HttpServletRequest request, HttpStatus status, String error, String message, String errorPage) {
        if (isApiRequest(request)) {
            return ResponseEntity.status(status)
                    .body(Map.of(
                        "status", status.value(),
                        "error", error,
                        "message", message,
                        "timestamp", System.currentTimeMillis()
                    ));
        }
        
        request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, status.value());
        return errorPage;
    }
    
    private boolean isApiRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String accept = request.getHeader("Accept");
        
        return uri.startsWith("/api/") || 
               (accept != null && accept.contains("application/json"));
    }
}