// src/main/java/com/posco/ptrs/domain/category/controller/CategoryController.java
package com.posco.ptrs.domain.category.controller;

import com.posco.ptrs.domain.category.dto.CategoryDto;
import com.posco.ptrs.domain.category.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryMapper categoryMapper;

    @GetMapping("/roots")
    public ResponseEntity<List<CategoryDto>> roots() {
        log.info("=== 루트 카테고리 조회 시작 ===");
        
        try {
            var data = categoryMapper.findRoots();
            log.info("루트 카테고리 조회 완료 - 결과 수: {}", data.size());
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            log.error("루트 카테고리 조회 실패: {}", e.getMessage());
            throw e; // GlobalExceptionHandler가 처리
        }
    }
    
    @GetMapping
    public ResponseEntity<List<CategoryDto>> children(@RequestParam String parentId) {
        log.info("=== 자식 카테고리 조회 시작 - parentId: {} ===", parentId);
        
        if (parentId == null || parentId.trim().isEmpty()) {
            throw new IllegalArgumentException("parentId는 필수입니다");
        }
        
        try {
            var data = categoryMapper.findByParentId(parentId);
            log.info("자식 카테고리 조회 완료 - parentId: {}, 결과 수: {}", parentId, data.size());
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            log.error("자식 카테고리 조회 실패 - parentId: {}, 오류: {}", parentId, e.getMessage());
            throw e; // GlobalExceptionHandler가 처리
        }
    }
}