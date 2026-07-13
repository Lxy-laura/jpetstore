package com.jpetstore.controller;

import com.jpetstore.domain.Category;
import com.jpetstore.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private com.jpetstore.mapper.AccountMapper accountMapper;

    @MockitoBean
    private com.jpetstore.mapper.CategoryMapper categoryMapper;

    @MockitoBean
    private com.jpetstore.mapper.ProductMapper productMapper;

    @MockitoBean
    private com.jpetstore.mapper.ItemMapper itemMapper;

    @MockitoBean
    private com.jpetstore.mapper.OrderMapper orderMapper;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setCatid("FISH");
        testCategory.setName("Fish");
        testCategory.setDescription("Various fish species");
    }

    // ==================== 获取所有分类 ====================

    @Test
    void testGetAllCategories() throws Exception {
        List<Category> categories = Arrays.asList(testCategory);
        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].catid").value("FISH"))
                .andExpect(jsonPath("$.data[0].name").value("Fish"))
                .andExpect(jsonPath("$.data[0].description").value("Various fish species"));
    }

    @Test
    void testGetAllCategoriesEmpty() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testGetAllCategoriesMultiple() throws Exception {
        Category cat1 = new Category("FISH", "Fish", "desc1");
        Category cat2 = new Category("DOGS", "Dogs", "desc2");
        Category cat3 = new Category("CATS", "Cats", "desc3");

        when(categoryService.getAllCategories()).thenReturn(Arrays.asList(cat1, cat2, cat3));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data[0].catid").value("FISH"))
                .andExpect(jsonPath("$.data[1].catid").value("DOGS"))
                .andExpect(jsonPath("$.data[2].catid").value("CATS"));
    }

    // ==================== 根据ID获取分类 ====================

    @Test
    void testGetCategoryById() throws Exception {
        when(categoryService.getCategoryById("FISH")).thenReturn(testCategory);

        mockMvc.perform(get("/api/categories/FISH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.catid").value("FISH"))
                .andExpect(jsonPath("$.data.name").value("Fish"))
                .andExpect(jsonPath("$.data.description").value("Various fish species"));
    }

    @Test
    void testGetCategoryByIdNotFound() throws Exception {
        when(categoryService.getCategoryById("NOTEXIST")).thenReturn(null);

        mockMvc.perform(get("/api/categories/NOTEXIST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("分类不存在"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"FISH", "DOGS", "CATS", "REPTILES", "BIRDS"})
    void testGetCategoryByDifferentIds(String catid) throws Exception {
        when(categoryService.getCategoryById(catid)).thenReturn(testCategory);

        mockMvc.perform(get("/api/categories/" + catid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // ==================== 创建分类 ====================

    @Test
    void testCreateCategory() throws Exception {
        when(categoryService.insertCategory(any(Category.class))).thenReturn(1);

        String json = "{\"catid\":\"INSECT\",\"name\":\"Insects\",\"description\":\"Various insect species\"}";

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("创建成功"));
    }

    @Test
    void testCreateCategoryFailure() throws Exception {
        when(categoryService.insertCategory(any(Category.class))).thenReturn(0);

        String json = "{\"catid\":\"INSECT\",\"name\":\"Insects\",\"description\":\"desc\"}";

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("创建失败"));
    }

    @Test
    void testCreateCategoryWithBlankId() throws Exception {
        String json = "{\"catid\":\"\",\"name\":\"Insects\"}";

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void testCreateCategoryWithBlankName() throws Exception {
        String json = "{\"catid\":\"INSECT\",\"name\":\"\"}";

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    // ==================== 更新分类 ====================

    @Test
    void testUpdateCategory() throws Exception {
        when(categoryService.updateCategory(any(Category.class))).thenReturn(1);

        String json = "{\"catid\":\"FISH\",\"name\":\"Freshwater Fish\",\"description\":\"Updated description\"}";

        mockMvc.perform(put("/api/categories/FISH")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("更新成功"));
    }

    @Test
    void testUpdateCategoryFailure() throws Exception {
        when(categoryService.updateCategory(any(Category.class))).thenReturn(0);

        String json = "{\"catid\":\"FISH\",\"name\":\"Updated\"}";

        mockMvc.perform(put("/api/categories/FISH")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("更新失败"));
    }

    // ==================== 删除分类 ====================

    @Test
    void testDeleteCategory() throws Exception {
        when(categoryService.deleteCategory("FISH")).thenReturn(1);

        mockMvc.perform(delete("/api/categories/FISH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"));
    }

    @Test
    void testDeleteCategoryFailure() throws Exception {
        when(categoryService.deleteCategory("FISH")).thenReturn(0);

        mockMvc.perform(delete("/api/categories/FISH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("删除失败"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"FISH", "DOGS", "CATS"})
    void testDeleteCategoryByDifferentIds(String catid) throws Exception {
        when(categoryService.deleteCategory(catid)).thenReturn(1);

        mockMvc.perform(delete("/api/categories/" + catid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}