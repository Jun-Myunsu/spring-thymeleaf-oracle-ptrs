package com.posco.ptrs.service;

import com.posco.ptrs.dto.UserDto;
import com.posco.ptrs.entity.User;
import com.posco.ptrs.mapper.UserMapper;
import com.posco.ptrs.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserMapper userMapper;
    
    @InjectMocks
    private UserService userService;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("테스트사용자")
                .email("test@posco.com")
                .password(PasswordUtil.encode("test123"))
                .role("USER")
                .build();
    }
    
    @Test
    @DisplayName("사용자 생성 테스트")
    void createUser() {
        // given
        var userDto = UserDto.of("테스트사용자", "test@posco.com", "test123", "USER");
        
        // when
        userService.createUser(userDto);
        
        // then
        verify(userMapper).insert(any(User.class));
    }
    
    @Test
    @DisplayName("로그인 인증 성공 테스트")
    void authenticate_success() {
        // given
        when(userMapper.findByEmail("test@posco.com")).thenReturn(testUser);
        
        // when
        boolean result = userService.authenticate("test@posco.com", "test123");
        
        // then
        assertThat(result).isTrue();
    }
    
    @Test
    @DisplayName("로그인 인증 실패 테스트 - 잘못된 비밀번호")
    void authenticate_fail_wrong_password() {
        // given
        when(userMapper.findByEmail("test@posco.com")).thenReturn(testUser);
        
        // when
        boolean result = userService.authenticate("test@posco.com", "wrong123");
        
        // then
        assertThat(result).isFalse();
    }
    
    @Test
    @DisplayName("로그인 인증 실패 테스트 - 존재하지 않는 사용자")
    void authenticate_fail_user_not_found() {
        // given
        when(userMapper.findByEmail("notfound@posco.com")).thenReturn(null);
        
        // when
        boolean result = userService.authenticate("notfound@posco.com", "test123");
        
        // then
        assertThat(result).isFalse();
    }
}