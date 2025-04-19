package com.example.mapper;

import com.example.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

/**
 * 操作orders相关数据接口
 */
@Mapper
public interface OrdersMapper {

  /**
   * 新增
   */
  int insert(Orders orders);

  /**
   * 删除
   */
  int deleteById(Integer id);

  /**
   * 修改
   */
  int updateById(Orders orders);

  /**
   * 根据ID查询
   */
  Orders selectById(Integer id);

  /**
   * 查询所有
   */
  List<Orders> selectAll(Orders orders);

  /**
   * 根据用户ID和商品ID查询订单
   */
  @Select("select * from orders where user_id = #{userId} and goods_id = #{goodsId}")
  Orders selectByUserIdAndGoodsId(@Param("userId") Integer userId, @Param("goodsId") Integer goodsId);

  /**
   * 查询所有已完成或已评价的订单
   */
  @Select("select * from orders where status = '已完成' or status = '已评价'")
  List<Orders> selectAllOKOrders();

  /**
   * 根据订单ID查询订单详情
   */
  Orders selectByOrderId(@Param("orderId") String orderId);

  /**
   * 更新订单的退货状态
   * 
   * @param orderId      订单ID
   * @param returnStatus 退货状态：0无 1处理中 2已完成
   * @return 更新的记录数
   */
  int updateReturnStatus(@Param("orderId") String orderId, @Param("returnStatus") String returnStatus);

  /**
   * 分页查询单个用户的订单
   * 
   * @param pageNum  页码
   * @param pageSize 每页大小
   * @return 订单列表
   */
  List<Orders> selectPageByUserId(@Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize,
      @Param("userId") Integer userId);

  /**
   * 根据用户ID查询该用户的所有订单
   * 
   * @param userId 用户ID
   * @return 订单列表（包含额外信息）
   */
  List<Map<String, Object>> selectByUserId(@Param("userId") Integer userId);

  /**
   * 查询商家的所有订单
   * 
   * @param businessId 商家ID
   * @return 订单列表
   */
  List<Map<String, Object>> selectByBusinessId(@Param("businessId") Integer businessId);

  /**
   * 分页查询商家订单
   * 
   * @param pageNum    页码
   * @param pageSize   每页大小
   * @param businessId 商家ID
   * @return 订单列表
   */
  List<Orders> selectPageForBusiness(@Param("pageNum") Integer pageNum,
      @Param("pageSize") Integer pageSize,
      @Param("businessId") Integer businessId);

  Integer selectCount(@Param("role") String role, @Param("userId") Integer userId, @Param("name") String name);

  List<Orders> selectPage(@Param("role") String role, @Param("userId") Integer userId, @Param("name") String name,
      @Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize);
}