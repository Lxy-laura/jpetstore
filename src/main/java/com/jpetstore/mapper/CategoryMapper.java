package com.jpetstore.mapper;

import com.jpetstore.domain.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper {

    List<Category> getAllCategories();

    Category getCategoryById(@Param("catid") String catid);

    int insertCategory(Category category);

    int updateCategory(Category category);

    int deleteCategory(@Param("catid") String catid);
}