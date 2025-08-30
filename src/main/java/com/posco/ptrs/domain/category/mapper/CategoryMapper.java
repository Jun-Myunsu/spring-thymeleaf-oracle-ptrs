// src/main/java/com/posco/ptrs/domain/category/mapper/CategoryMapper.java
package com.posco.ptrs.domain.category.mapper;

import com.posco.ptrs.domain.category.dto.CategoryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {
    
    @Select("""
        SELECT id, name, level, parent_id as parentId,
               CASE WHEN EXISTS(SELECT 1 FROM categories c2 WHERE c2.parent_id = c.id) 
                    THEN true ELSE false END as hasChildren
        FROM categories c 
        WHERE parent_id IS NULL 
        ORDER BY sort_order
        """)
    List<CategoryDto> findRoots();
    
    @Select("""
        SELECT id, name, level, parent_id as parentId,
               CASE WHEN EXISTS(SELECT 1 FROM categories c2 WHERE c2.parent_id = c.id) 
                    THEN true ELSE false END as hasChildren
        FROM categories c 
        WHERE parent_id = #{parentId} 
        ORDER BY sort_order
        """)
    List<CategoryDto> findByParentId(String parentId);
}