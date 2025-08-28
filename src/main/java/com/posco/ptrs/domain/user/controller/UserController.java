package com.posco.ptrs.domain.user.controller;

import com.posco.ptrs.domain.user.dto.UserDto;
import com.posco.ptrs.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "user/list";
    }
    
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("userDto", UserDto.of("", "", "", "USER"));
        return "user/form";
    }
    
    @PostMapping
    public String create(@Valid @ModelAttribute UserDto userDto, 
                        BindingResult result, 
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "user/form";
        }
        
        try {
            userService.createUser(userDto);
            redirectAttributes.addFlashAttribute("message", "사용자가 성공적으로 생성되었습니다.");
            return "redirect:/users";
        } catch (Exception e) {
            result.rejectValue("email", "duplicate", "이미 존재하는 이메일입니다.");
            return "user/form";
        }
    }
    
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        return userService.getUserById(id)
                .map(user -> {
                    model.addAttribute("user", user);
                    return "user/detail";
                })
                .orElse("redirect:/users");
    }
    
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        return userService.getUserById(id)
                .map(user -> {
                    model.addAttribute("userDto", UserDto.from(user));
                    model.addAttribute("isEdit", true);
                    return "user/form";
                })
                .orElse("redirect:/users");
    }
    
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                        @Valid @ModelAttribute UserDto userDto,
                        BindingResult result,
                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "user/form";
        }
        
        try {
            userService.updateUser(id, userDto);
            redirectAttributes.addFlashAttribute("message", "사용자 정보가 수정되었습니다.");
            return "redirect:/users/" + id;
        } catch (Exception e) {
            result.rejectValue("email", "error", "수정 중 오류가 발생했습니다.");
            return "user/form";
        }
    }
    
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("message", "사용자가 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "삭제 중 오류가 발생했습니다.");
        }
        return "redirect:/users";
    }
}