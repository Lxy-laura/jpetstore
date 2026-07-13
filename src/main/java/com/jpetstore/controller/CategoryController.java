package com.jpetstore.controller;

import com.jpetstore.common.Result;
import com.jpetstore.domain.Category;
import com.jpetstore.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public Result<List<Category>> getAllCategories() {
        return Result.success(categoryService.getAllCategories());
    }

    @GetMapping("/{catid}")
    public Result<Category> getCategoryById(@PathVariable String catid) {
        Category category = categoryService.getCategoryById(catid);
        if (category != null) {
            return Result.success(category);
        }
        return Result.notFound("分类不存在");
    }

    @PostMapping
    public Result<String> createCategory(@Valid @RequestBody Category category) {
        int result = categoryService.insertCategory(category);
        if (result > 0) {
            return Result.success("创建成功", "创建成功");
        }
        return Result.error(503, "创建失败");
    }

    @PutMapping("/{catid}")
    public Result<String> updateCategory(@PathVariable String catid, @Valid @RequestBody Category category) {
        category.setCatid(catid);
        int result = categoryService.updateCategory(category);
        if (result > 0) {
            return Result.success("更新成功", "更新成功");
        }
        return Result.error(503, "更新失败");
    }

    @DeleteMapping("/{catid}")
    public Result<String> deleteCategory(@PathVariable String catid) {
        int result = categoryService.deleteCategory(catid);
        if (result > 0) {
            return Result.success("删除成功", "删除成功");
        }
        return Result.error(503, "删除失败");
    }
}