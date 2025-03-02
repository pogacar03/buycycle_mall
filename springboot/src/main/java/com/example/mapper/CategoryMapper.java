package com.example.mapper;

import com.example.entity.Category;
import com.example.entity.Type;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CategoryMapper {
     List<Category> selectAll(Category category);

    // 根据父分类 id 查询子分类
    List<Category> selectByParentId(@Param("parentId") Integer parentId);
}