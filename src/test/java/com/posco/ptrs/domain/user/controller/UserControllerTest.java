package com.posco.ptrs.domain.user.controller;

import com.posco.ptrs.domain.user.service.UserService;
import com.posco.ptrs.domain.user.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("사용자 목록 조회 성공")
    void getUserList() throws Exception {
        // given
        List<UserDto> mockUsers = List.of(
            new UserDto(1L, "admin", "admin@posco.com", "ADMIN", null, null, null),
            new UserDto(2L, "user1", "user1@posco.com", "USER", null, null, null)
        );
        given(userService.getAllUsers()).willReturn(mockUsers);

        // when & then
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("users"))
                .andExpect(view().name("user/list"));
    }

    @Test
    @DisplayName("사용자 상세 조회 성공")
    void getUserDetail() throws Exception {
        // given
        Long userId = 1L;
        UserDto mockUser = new UserDto(1L, "admin", "admin@posco.com", "ADMIN", null, null, null);
        given(userService.getUserById(userId)).willReturn(mockUser);

        // when & then
        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("user/detail"));
    }

    @Test
    @DisplayName("사용자 생성 폼 조회")
    void getUserCreateForm() throws Exception {
        mockMvc.perform(get("/users/new"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("user/form"));
    }

    @Test
    @DisplayName("사용자 생성 성공")
    void createUser() throws Exception {
        // given
        UserDto newUser = new UserDto(null, "test", "test@posco.com", "USER", "password", null, null);
        UserDto savedUser = new UserDto(3L, "test", "test@posco.com", "USER", null, null, null);
        given(userService.createUser(any(UserDto.class))).willReturn(savedUser);

        // when & then
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "test")
                .param("email", "test@posco.com")
                .param("role", "USER")
                .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
    }

    @Test
    @DisplayName("사용자 수정 폼 조회")
    void getUserEditForm() throws Exception {
        // given
        Long userId = 1L;
        UserDto mockUser = new UserDto(1L, "admin", "admin@posco.com", "ADMIN", null, null, null);
        given(userService.getUserById(userId)).willReturn(mockUser);

        // when & then
        mockMvc.perform(get("/users/{id}/edit", userId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("user/form"));
    }

    @Test
    @DisplayName("사용자 수정 성공")
    void updateUser() throws Exception {
        // given
        Long userId = 1L;
        UserDto updatedUser = new UserDto(1L, "admin", "admin@posco.com", "ADMIN", null, null, null);
        given(userService.updateUser(eq(userId), any(UserDto.class))).willReturn(updatedUser);

        // when & then
        mockMvc.perform(put("/users/{id}", userId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "admin")
                .param("email", "admin@posco.com")
                .param("role", "ADMIN"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
    }

    @Test
    @DisplayName("사용자 삭제 성공")
    void deleteUser() throws Exception {
        // given
        Long userId = 1L;
        doNothing().when(userService).deleteUser(userId);

        // when & then
        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
    }

    @Test
    @DisplayName("사용자 생성 검증 실패")
    void createUserValidationFail() throws Exception {
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "")
                .param("email", "invalid-email")
                .param("role", "USER"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/form"));
    }
}