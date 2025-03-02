package com.example.controller;

import com.example.common.Result;
import com.example.entity.Category;
import com.example.entity.Type;
import com.example.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/children/{parentId}")
    public List<Category> getChildrenByParentId(@PathVariable Integer parentId) {
        return categoryService.getChildrenByParentId(parentId);
    }
    @GetMapping("/selectAll")
    public Result selectAll(Category category ) {
        List<Category> list = CategoryService.selectAll(category);
        return Result.success(list);
    }
}