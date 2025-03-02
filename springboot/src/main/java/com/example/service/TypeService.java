package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.entity.Account;
import com.example.entity.Type;
import com.example.mapper.TypeMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 公告信息表业务处理
 **/
@Service
public interface TypeService {

    /**
     * 新增
     */
    void add(Type type);

    /**
     * 删除
     */
    void deleteById(Integer id);

    /**
     * 批量删除
     */
    void deleteBatch(List<Integer> ids);

    /**
     * 修改
     */
    void updateById(Type type);

    /**
     * 根据ID查询
     */
    Type selectById(Integer id);

    /**
     * 查询所有
     */
    List<Type> selectAll(Type type);

    /**
     * 分页查询
     */
    PageInfo<Type> selectPage(Type type, Integer pageNum, Integer pageSize);

    /**
     * 获取某个分类的所有子分类
     */
    List<Type> getChildrenTypes(Integer parentId);
}