package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Cart;
import com.example.entity.Goods;
import com.example.entity.Orders;
import com.example.mapper.CartMapper;
import com.example.mapper.GoodsMapper;
import com.example.mapper.OrdersMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.example.exception.CustomException;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 收藏业务处理
 **/
@Service
public class OrdersService {

    @Resource
    OrdersMapper ordersMapper;
    @Resource
    private CartMapper cartMapper;
    @Resource
    private GoodsMapper goodsMapper;

    /**
     * 新增
     */
    public void add(Orders orders) {
        orders.setOrderId(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
        for (Cart cart : orders.getCartData()) {
            // 检查库存
            Goods goods = goodsMapper.selectById(cart.getGoodsId());
            if (goods == null || goods.getStock() == null || goods.getStock() < cart.getNum()) {
                throw new CustomException("5008", "商品 " + goods.getName() + " 库存不足");
            }

            // 创建订单记录
            Orders dbOrders = new Orders();
            BeanUtils.copyProperties(orders, dbOrders);
            dbOrders.setGoodsId(cart.getGoodsId());
            dbOrders.setBusinessId(cart.getBusinessId());
            dbOrders.setNum(cart.getNum());
            dbOrders.setPrice(cart.getNum() * cart.getGoodsPrice());

            // 设置初始订单状态为"待支付"
            dbOrders.setStatus("待支付");
            dbOrders.setPayStatus("未支付");

            ordersMapper.insert(dbOrders);

            // 把购物车里对应的商品删掉
            cartMapper.deleteById(cart.getId());

            // 增加销量
            goods.setCount(goods.getCount() + cart.getNum());

            // 注意：这里暂不减少库存，而是在支付成功后减少库存
            goodsMapper.updateById(goods);
        }
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        ordersMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            ordersMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Orders orders) {
        ordersMapper.updateById(orders);
    }

    /**
     * 根据ID查询
     */
    public Orders selectById(Integer id) {
        return ordersMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Orders> selectAll(Orders orders) {
        return ordersMapper.selectAll(orders);
    }

    /**
     * 分页查询
     */
    public PageInfo<Map<String, Object>> selectPage(Integer pageNum, Integer pageSize, Integer userId) {
        PageHelper.startPage(pageNum, pageSize);
        List<Map<String, Object>> orders = ordersMapper.selectByUserId(userId);
        return new PageInfo<>(orders);
    }

    public PageInfo<Map<String, Object>> selectPageForBusiness(Integer pageNum, Integer pageSize, Integer businessId) {
        PageHelper.startPage(pageNum, pageSize);
        List<Map<String, Object>> orders = ordersMapper.selectByBusinessId(businessId);
        return new PageInfo<>(orders);
    }

    /**
     * 更新订单支付状态
     */
    public boolean updatePayStatus(String orderId, String payStatus) {
        try {
            Orders order = ordersMapper.selectByOrderId(orderId);
            if (order != null) {
                order.setPayStatus(payStatus);

                // 如果是设置为已支付，还需要更新订单状态为待发货
                if ("已支付".equals(payStatus)) {
                    order.setStatus("待发货");

                    // 获取商品信息并减少库存
                    Goods goods = goodsMapper.selectById(order.getGoodsId());
                    if (goods != null && goods.getStock() != null) {
                        // 减少商品库存
                        int quantity = -order.getNum(); // 负数表示减少库存
                        boolean stockUpdated = updateStockQuantity(goods.getId(), quantity);
                        if (!stockUpdated) {
                            // 库存不足或更新失败
                            System.out.println("商品库存不足或更新失败: " + goods.getName());
                        }
                    }
                }
                ordersMapper.updateById(order);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新商品库存
     * 
     * @param goodsId  商品ID
     * @param quantity 变更数量（正数增加，负数减少）
     * @return 是否更新成功
     */
    private boolean updateStockQuantity(Integer goodsId, Integer quantity) {
        try {
            Goods goods = goodsMapper.selectById(goodsId);
            if (goods == null) {
                return false;
            }

            // 如果是减库存操作，检查库存是否足够
            if (quantity < 0 && (goods.getStock() == null || goods.getStock() < Math.abs(quantity))) {
                return false;
            }

            // 更新库存
            Integer newStock = goods.getStock() == null ? quantity : goods.getStock() + quantity;
            goods.setStock(newStock < 0 ? 0 : newStock);
            goodsMapper.updateById(goods);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}