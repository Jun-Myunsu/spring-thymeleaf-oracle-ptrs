// src/main/java/com/posco/ptrs/domain/category/service/CategoryService.java
package com.posco.ptrs.domain.category.service;

import com.posco.ptrs.domain.category.dto.CategoryDto;
import com.posco.ptrs.domain.category.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryMapper categoryMapper;
    
    public List<CategoryDto> getRootCategories() {
        return categoryMapper.findRoots();
    }
    
    public List<CategoryDto> getChildCategories(String parentId) {
        return categoryMapper.findByParentId(parentId);
    }
}