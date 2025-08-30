// src/main/java/com/posco/ptrs/domain/category/controller/CategoryController.java
package com.posco.ptrs.domain.category.controller;

import com.posco.ptrs.domain.category.dto.CategoryDto;
import com.posco.ptrs.domain.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/roots")
    public ResponseEntity<List<CategoryDto>> roots() {
        log.info("=== 루트 카테고리 조회 시작 ===");
        
        var data = categoryService.getRootCategories();
        log.info("루트 카테고리 조회 완룈 - 결과 수: {}", data.size());
        return ResponseEntity.ok(data);
    }
    
    @GetMapping
    public ResponseEntity<List<CategoryDto>> children(@RequestParam @NotBlank(message = "parentId는 필수입니다") String parentId) {
        log.info("=== 자식 카테곣고리 조회 시작 - parentId: {} ===", parentId);
        
        var data = categoryService.getChildCategories(parentId);
        log.info("자식 카테고리 조회 완룈 - parentId: {}, 결과 수: {}", parentId, data.size());
        return ResponseEntity.ok(data);
    }
}