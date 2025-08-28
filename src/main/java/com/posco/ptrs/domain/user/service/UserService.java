package com.posco.ptrs.domain.user.service;

import com.posco.ptrs.domain.user.dto.UserDto;
import com.posco.ptrs.domain.user.entity.User;
import com.posco.ptrs.domain.user.mapper.UserMapper;
import com.posco.ptrs.global.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    
    private final UserMapper userMapper;
    
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userMapper.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(userMapper.findById(id));
    }
    
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return Optional.ofNullable(userMapper.findByEmail(email));
    }
    
    public User createUser(UserDto userDto) {
        var user = User.builder()
                .username(userDto.username())
                .email(userDto.email())
                .password(PasswordUtil.encode(userDto.password()))
                .role(Optional.ofNullable(userDto.role()).orElse("USER"))
                .build();
        
        userMapper.insert(user);
        return user;
    }
    
    public User updateUser(Long id, UserDto userDto) {
        var user = getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + id));
        
        user.setUsername(userDto.username());
        user.setEmail(userDto.email());
        
        Optional.ofNullable(userDto.password())
                .filter(pwd -> !pwd.isBlank())
                .ifPresent(pwd -> user.setPassword(PasswordUtil.encode(pwd)));
                
        user.setRole(userDto.role());
        user.setUpdatedAt(LocalDateTime.now());
        
        userMapper.update(user);
        return user;
    }
    
    public void deleteUser(Long id) {
        if (getUserById(id).isEmpty()) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다: " + id);
        }
        userMapper.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public boolean authenticate(String email, String password) {
        return getUserByEmail(email)
                .map(user -> PasswordUtil.matches(password, user.getPassword()))
                .orElse(false);
    }
}