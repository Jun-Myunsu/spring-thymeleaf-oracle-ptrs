package com.posco.ptrs.global.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
    
    public UserNotFoundException(Long id) {
        super("사용자를 찾을 수 없습니다: " + id);
    }
}