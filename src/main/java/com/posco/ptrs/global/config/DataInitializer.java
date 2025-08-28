package com.posco.ptrs.global.config;

import com.posco.ptrs.domain.user.entity.User;
import com.posco.ptrs.domain.user.mapper.UserMapper;
import com.posco.ptrs.global.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final UserMapper userMapper;
    
    @Override
    public void run(String... args) {
        try {
            if (userMapper.count() == 0) {
                initializeUsers();
            }
        } catch (Exception e) {
            log.info("테이블이 존재하지 않아 초기 데이터 생성을 건너뜁니다.");
        }
    }
    
    private void initializeUsers() {
        var admin = User.builder()
                .username("관리자")
                .email("admin@posco.com")
                .password(PasswordUtil.encode("admin123"))
                .role("ADMIN")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        var user1 = User.builder()
                .username("사용자1")
                .email("user1@posco.com")
                .password(PasswordUtil.encode("user123"))
                .role("USER")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        var user2 = User.builder()
                .username("사용자2")
                .email("user2@posco.com")
                .password(PasswordUtil.encode("user123"))
                .role("USER")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        userMapper.insert(admin);
        userMapper.insert(user1);
        userMapper.insert(user2);
        
        log.info("초기 사용자 데이터 생성 완료");
        log.info("테스트 계정 - admin@posco.com / admin123");
        log.info("테스트 계정 - user1@posco.com / user123");
    }
}