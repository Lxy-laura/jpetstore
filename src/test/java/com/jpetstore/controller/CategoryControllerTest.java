package com.jpetstore.controller;

import com.jpetstore.domain.Category;
import com.jpetstore.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setCatid("TEST");
        testCategory.setName("Test Category");
        testCategory.setDescription("Test Description");
    }

    @Test
    void testGetAllCategories() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(Arrays.asList(testCategory));
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testGetCategoryById() throws Exception {
        when(categoryService.getCategoryById("TEST")).thenReturn(testCategory);
        mockMvc.perform(get("/api/categories/TEST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.catid").value("TEST"));
    }

    @Test
    void testGetCategoryByIdNotFound() throws Exception {
        when(categoryService.getCategoryById("NOTEXIST")).thenReturn(null);
        mockMvc.perform(get("/api/categories/NOTEXIST"))
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void testCreateCategory() throws Exception {
        when(categoryService.insertCategory(any(Category.class))).thenReturn(1);
        String json = "{\"catid\":\"TEST\",\"name\":\"Test Category\",\"description\":\"Test Description\"}";
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testUpdateCategory() throws Exception {
        when(categoryService.updateCategory(any(Category.class))).thenReturn(1);
        String json = "{\"catid\":\"TEST\",\"name\":\"Updated Category\",\"description\":\"Updated Description\"}";
        mockMvc.perform(put("/api/categories/TEST")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testDeleteCategory() throws Exception {
        when(categoryService.deleteCategory("TEST")).thenReturn(1);
        mockMvc.perform(delete("/api/categories/TEST"))
                .andExpect(jsonPath("$.code").value(200));
    }
}