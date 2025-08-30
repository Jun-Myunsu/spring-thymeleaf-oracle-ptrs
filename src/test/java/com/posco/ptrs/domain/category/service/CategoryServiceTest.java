package com.posco.ptrs.domain.category.service;

import com.posco.ptrs.domain.category.mapper.CategoryMapper;
import com.posco.ptrs.domain.category.dto.CategoryDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    @DisplayName("루트 카테고리 조회 성공")
    void getRootCategories() {
        // given
        List<CategoryDto> mockData = List.of(
            new CategoryDto("posco", "포스코", 1, null, true),
            new CategoryDto("samsung", "삼성", 1, null, false)
        );
        given(categoryMapper.findRoots()).willReturn(mockData);

        // when
        List<CategoryDto> result = categoryService.getRootCategories();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo("posco");
        assertThat(result.get(0).name()).isEqualTo("포스코");
        assertThat(result.get(0).level()).isEqualTo(1);
        assertThat(result.get(0).hasChildren()).isTrue();
    }

    @Test
    @DisplayName("자식 카테고리 조회 성공")
    void getChildCategories() {
        // given
        String parentId = "posco";
        List<CategoryDto> mockData = List.of(
            new CategoryDto("posco-tech", "기술부분", 2, "posco", true)
        );
        given(categoryMapper.findByParentId(parentId)).willReturn(mockData);

        // when
        List<CategoryDto> result = categoryService.getChildCategories(parentId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).parentId()).isEqualTo("posco");
        assertThat(result.get(0).level()).isEqualTo(2);
    }

    @Test
    @DisplayName("빈 결과 반환")
    void getEmptyResult() {
        // given
        given(categoryMapper.findRoots()).willReturn(List.of());

        // when
        List<CategoryDto> result = categoryService.getRootCategories();

        // then
        assertThat(result).isEmpty();
    }
}