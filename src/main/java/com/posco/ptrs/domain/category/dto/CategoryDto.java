// src/main/java/com/posco/ptrs/domain/category/dto/CategoryDto.java
package com.posco.ptrs.domain.category.dto;

public record CategoryDto(
    String id,
    String name,
    int level,
    String parentId,
    boolean hasChildren
) {}