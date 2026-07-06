package com.jpetstore.service;

import com.jpetstore.domain.Category;
import com.jpetstore.mapper.CategoryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category fishCategory;
    private Category dogsCategory;

    @BeforeEach
    void setUp() {
        fishCategory = new Category("FISH", "Fish", "Various fish species");
        dogsCategory = new Category("DOGS", "Dogs", "Different dog breeds");
    }

    @Test
    void testGetAllCategories() {
        List<Category> categories = Arrays.asList(fishCategory, dogsCategory);
        when(categoryMapper.getAllCategories()).thenReturn(categories);

        List<Category> result = categoryService.getAllCategories();

        assertEquals(2, result.size());
        assertEquals("FISH", result.get(0).getCatid());
        assertEquals("DOGS", result.get(1).getCatid());
        verify(categoryMapper, times(1)).getAllCategories();
    }

    @Test
    void testGetCategoryById() {
        when(categoryMapper.getCategoryById("FISH")).thenReturn(fishCategory);

        Category result = categoryService.getCategoryById("FISH");

        assertNotNull(result);
        assertEquals("FISH", result.getCatid());
        assertEquals("Fish", result.getName());
        verify(categoryMapper, times(1)).getCategoryById("FISH");
    }

    @Test
    void testGetCategoryByIdNotFound() {
        when(categoryMapper.getCategoryById("NOT_EXIST")).thenReturn(null);

        Category result = categoryService.getCategoryById("NOT_EXIST");

        assertNull(result);
    }

    @Test
    void testInsertCategory() {
        when(categoryMapper.insertCategory(fishCategory)).thenReturn(1);

        int result = categoryService.insertCategory(fishCategory);

        assertEquals(1, result);
        verify(categoryMapper, times(1)).insertCategory(fishCategory);
    }

    @Test
    void testUpdateCategory() {
        fishCategory.setDescription("Updated description");
        when(categoryMapper.updateCategory(fishCategory)).thenReturn(1);

        int result = categoryService.updateCategory(fishCategory);

        assertEquals(1, result);
        verify(categoryMapper, times(1)).updateCategory(fishCategory);
    }

    @Test
    void testDeleteCategory() {
        when(categoryMapper.deleteCategory("FISH")).thenReturn(1);

        int result = categoryService.deleteCategory("FISH");

        assertEquals(1, result);
        verify(categoryMapper, times(1)).deleteCategory("FISH");
    }

    @Test
    void testDeleteCategoryNotFound() {
        when(categoryMapper.deleteCategory("NOT_EXIST")).thenReturn(0);

        int result = categoryService.deleteCategory("NOT_EXIST");

        assertEquals(0, result);
    }
}