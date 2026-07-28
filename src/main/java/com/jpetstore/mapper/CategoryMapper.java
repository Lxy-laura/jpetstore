package com.jpetstore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jpetstore.domain.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    @Select("SELECT * FROM category WHERE catid = #{catid}")
    Category getCategoryById(String catid);

    @Select("SELECT * FROM category ORDER BY catid")
    List<Category> getAllCategories();
    
    default int insertCategory(Category category) { return ((com.baomidou.mybatisplus.core.mapper.BaseMapper<Category>)this).insert(category); }
    default int updateCategory(Category category) { return ((com.baomidou.mybatisplus.core.mapper.BaseMapper<Category>)this).updateById(category); }
    default int deleteCategory(String catid) { return ((com.baomidou.mybatisplus.core.mapper.BaseMapper<Category>)this).deleteById(catid); }
}
