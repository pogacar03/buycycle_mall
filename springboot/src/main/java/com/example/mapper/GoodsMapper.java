package com.example.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.entity.Goods;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 操作goods相关数据接口
*/
public interface GoodsMapper {

    /**
      * 新增
    */
    int insert(Goods goods);

    /**
      * 删除
    */
    int deleteById(Integer id);

    /**
      * 修改
    */
    int updateById(Goods goods);

    /**
      * 根据ID查询
    */
    Goods selectById(Integer id);

    /**
      * 查询所有
    */
    List<Goods> selectAll(Goods goods);

    @Select("select * from goods order by count desc limit 15")
    List<Goods> selectTop15();

    @Select("select * from goods where type_id = #{id}")
    List<Goods> selectByTypeId(Integer id);
    @Select("select * from goods where business_id = #{id}")
    List<Goods> selectByBusinessId(Integer id);
    @Select("select * from goods where name like concat('%', #{name}, '%')")
    List<Goods> selectByName(String name);
    /**
     * 查询所有商品分类
     */
    @Select("SELECT DISTINCT category FROM goods WHERE category IS NOT NULL")
    List<String> selectAllCategories();

    List<Goods> selectList(QueryWrapper<Goods> wrapper);

    @Select("SELECT COUNT(*) FROM goods")
    int selectCount(Object o);

    /**
     * 根据价格范围查询商品
     */
    @Select("SELECT * FROM goods WHERE price >= #{minPrice} AND price <= #{maxPrice}")
    List<Goods> selectByPriceRange(@Param("minPrice") double minPrice, @Param("maxPrice") double maxPrice);
}