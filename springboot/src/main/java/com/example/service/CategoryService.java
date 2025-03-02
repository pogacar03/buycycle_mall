package com.example.service;

import com.example.entity.Category;
import com.example.entity.Type;
import com.example.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private static CategoryMapper categoryMapper;



    public static List<Category> selectAll(Category category) {
        // 通过实例调用非静态方法
        return categoryMapper.selectAll(category);
    }

    public List<Category> getChildrenByParentId(Integer parentId) {
        return categoryMapper.selectByParentId(parentId);
    }
}