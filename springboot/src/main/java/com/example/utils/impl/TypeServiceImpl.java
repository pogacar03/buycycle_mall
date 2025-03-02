package com.example.utils.impl;

import com.example.entity.Type;
import com.example.mapper.TypeMapper;
import com.example.service.TypeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service
public class TypeServiceImpl implements TypeService {
    @Resource
    private TypeMapper typeMapper;
    @Override
    public void add(Type type) {
        typeMapper.insert(type);
    }

    @Override
    public void deleteById(Integer id) {
        typeMapper.deleteById(id);
    }

    @Override
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            typeMapper.deleteById(id);
        }
    }

    @Override
    public void updateById(Type type) {
        typeMapper.updateById(type);
    }

    @Override
    public Type selectById(Integer id) {
        return typeMapper.selectById(id);
    }

    @Override
    public List<Type> selectAll(Type type) {
        return typeMapper.selectAll(type);
    }

    @Override
    public PageInfo<Type> selectPage(Type type, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Type> list = typeMapper.selectAll(type);
        return PageInfo.of(list);
    }

    @Override
    public List<Type> getChildrenTypes(Integer parentId) {
        return typeMapper.getChildrenTypes(parentId);
    }
}
