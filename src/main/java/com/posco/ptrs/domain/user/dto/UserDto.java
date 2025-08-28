package com.posco.ptrs.domain.user.dto;

import com.posco.ptrs.domain.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserDto(
    Long id,
    
    @NotBlank(message = "사용자명은 필수입니다")
    @Size(min = 2, max = 50, message = "사용자명은 2-50자 사이여야 합니다")
    String username,
    
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    String email,
    
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다")
    String password,
    
    String role
) {
    public static UserDto of(String username, String email, String password, String role) {
        return new UserDto(null, username, email, password, role);
    }
    
    public static UserDto from(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getEmail(), null, user.getRole());
    }
}