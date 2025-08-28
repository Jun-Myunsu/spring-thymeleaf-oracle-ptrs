package com.posco.ptrs.global.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.posco.ptrs.domain.*.mapper")
public class MyBatisConfig {
    // MyBatis 자동 설정 사용 - application.yml에서 설정
}