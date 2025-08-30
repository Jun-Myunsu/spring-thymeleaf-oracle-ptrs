package com.posco.ptrs.domain.user.controller;

import com.posco.ptrs.domain.user.service.UserService;
import com.posco.ptrs.domain.user.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    void 사용자_목록_조회_성공() throws Exception {
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
    void 사용자_상세_조회_성공() throws Exception {
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
    void 사용자_생성_폼_조회() throws Exception {
        mockMvc.perform(get("/users/new"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("user/form"));
    }

    @Test
    void 사용자_생성_성공() throws Exception {
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
    void 사용자_수정_폼_조회() throws Exception {
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
    void 사용자_수정_성공() throws Exception {
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
                .andExpected(redirectedUrl("/users"));
    }

    @Test
    void 사용자_삭제_성공() throws Exception {
        // given
        Long userId = 1L;
        doNothing().when(userService).deleteUser(userId);

        // when & then
        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().is3xxRedirection())
                .andExpected(redirectedUrl("/users"));
    }

    @Test
    void 사용자_생성_검증_실패() throws Exception {
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "")
                .param("email", "invalid-email")
                .param("role", "USER"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/form"));
    }
}