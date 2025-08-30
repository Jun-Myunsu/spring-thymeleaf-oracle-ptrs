package com.posco.ptrs.domain.category.controller;

import com.posco.ptrs.domain.category.service.CategoryService;
import com.posco.ptrs.domain.category.dto.CategoryDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    void 루트_카테고리_조회_성공() throws Exception {
        // given
        List<CategoryDto> mockData = List.of(
            new CategoryDto("posco", "포스코", 1, null, true),
            new CategoryDto("samsung", "삼성", 1, null, true)
        );
        given(categoryService.getRootCategories()).willReturn(mockData);

        // when & then
        mockMvc.perform(get("/api/categories/roots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("posco"))
                .andExpect(jsonPath("$[0].name").value("포스코"))
                .andExpect(jsonPath("$[0].level").value(1))
                .andExpect(jsonPath("$[0].hasChildren").value(true));
    }

    @Test
    void 자식_카테고리_조회_성공() throws Exception {
        // given
        String parentId = "posco";
        List<CategoryDto> mockData = List.of(
            new CategoryDto("posco-tech", "기술부분", 2, "posco", true)
        );
        given(categoryService.getChildCategories(parentId)).willReturn(mockData);

        // when & then
        mockMvc.perform(get("/api/categories").param("parentId", parentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("posco-tech"))
                .andExpect(jsonPath("$[0].parentId").value("posco"));
    }

    @Test
    void parentId_빈값시_400() throws Exception {
        mockMvc.perform(get("/api/categories").param("parentId", ""))
                .andExpect(status().isBadRequest());
    }
}