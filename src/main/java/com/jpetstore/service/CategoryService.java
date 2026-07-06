package com.jpetstore.service;

import com.jpetstore.domain.Category;
import com.jpetstore.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> getAllCategories() {
        return categoryMapper.getAllCategories();
    }

    public Category getCategoryById(String catid) {
        return categoryMapper.getCategoryById(catid);
    }

    @Transactional
    public int insertCategory(Category category) {
        return categoryMapper.insertCategory(category);
    }

    @Transactional
    public int updateCategory(Category category) {
        return categoryMapper.updateCategory(category);
    }

    @Transactional
    public int deleteCategory(String catid) {
        return categoryMapper.deleteCategory(catid);
    }
}