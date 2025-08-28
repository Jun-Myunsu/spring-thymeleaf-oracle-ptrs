package com.posco.ptrs.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PasswordUtilTest {
    
    @Test
    @DisplayName("비밀번호 암호화 테스트")
    void encode() {
        // given
        String password = "test123";
        
        // when
        String encoded = PasswordUtil.encode(password);
        
        // then
        assertThat(encoded).isNotNull();
        assertThat(encoded).isNotEqualTo(password);
    }
    
    @Test
    @DisplayName("비밀번호 검증 성공 테스트")
    void matches_success() {
        // given
        String password = "admin123";
        String encoded = PasswordUtil.encode(password);
        
        // when
        boolean result = PasswordUtil.matches(password, encoded);
        
        // then
        assertThat(result).isTrue();
    }
    
    @Test
    @DisplayName("비밀번호 검증 실패 테스트")
    void matches_fail() {
        // given
        String password = "admin123";
        String wrongPassword = "wrong123";
        String encoded = PasswordUtil.encode(password);
        
        // when
        boolean result = PasswordUtil.matches(wrongPassword, encoded);
        
        // then
        assertThat(result).isFalse();
    }
    
    @Test
    @DisplayName("실제 테스트 계정 비밀번호 검증")
    void test_actual_passwords() {
        // 실제 사용할 비밀번호들 테스트
        String adminPassword = "admin123";
        String userPassword = "user123";
        
        String encodedAdmin = PasswordUtil.encode(adminPassword);
        String encodedUser = PasswordUtil.encode(userPassword);
        
        assertThat(PasswordUtil.matches(adminPassword, encodedAdmin)).isTrue();
        assertThat(PasswordUtil.matches(userPassword, encodedUser)).isTrue();
        assertThat(PasswordUtil.matches("wrong", encodedAdmin)).isFalse();
    }
}