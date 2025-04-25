package com.example.controller;

import com.example.common.Result;
import com.example.common.enums.ResultCodeEnum;
import com.example.entity.Orders;
import com.example.entity.User;
import com.example.mapper.OrdersMapper;
import com.example.service.OrdersService;
import com.example.service.UserService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.example.exception.CustomException;

/**
 * 收藏前端操作接口
 **/
@RestController
@RequestMapping("/orders")
public class OrdersController {

    @Resource
    private OrdersService ordersService;
    @Resource
    private OrdersMapper ordersMapper;
    @Resource
    private UserService userService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Orders orders) {
        try {
            ordersService.add(orders);
            return Result.success();
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("500", "下单失败：" + e.getMessage());
        }
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        ordersService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        ordersService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Orders orders) {
        ordersService.updateById(orders);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Orders orders = ordersService.selectById(id);
        return Result.success(orders);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Orders orders) {
        List<Orders> list = ordersService.selectAll(orders);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "") String name,
            HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute("user");
        if (userObj == null) {
            userObj = request.getSession().getAttribute("business");
        }
        if (userObj == null) {
            userObj = request.getSession().getAttribute("admin");
        }

        if (userObj == null) {
            return Result.error("401", "未登录");
        }

        String role = "";
        Integer userId = null;

        // 判断用户类型并获取角色和ID
        if (userObj instanceof User) {
            User user = (User) userObj;
            role = "USER";
            userId = user.getId();
        } else if (userObj instanceof com.example.entity.Business) {
            com.example.entity.Business business = (com.example.entity.Business) userObj;
            role = "BUSINESS";
            userId = business.getId();
        } else if (userObj instanceof com.example.entity.Admin) {
            com.example.entity.Admin admin = (com.example.entity.Admin) userObj;
            role = "ADMIN";
            userId = admin.getId();
        }

        // 根据角色和ID查询订单
        Integer count = ordersMapper.selectCount(role, userId, name);
        List<Orders> list = ordersMapper.selectPage(role, userId, name, (pageNum - 1) * pageSize, pageSize);

        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", count);

        return Result.success(data);
    }

    @GetMapping("/detail/{orderId}")
    public Result detail(@PathVariable String orderId) {
        Orders order = ordersMapper.selectByOrderId(orderId);
        if (order == null) {
            // 不要使用 ResultCodeEnum.valueOf("订单不存在")
            // 改为直接使用错误码和消息
            return Result.error("404", "订单不存在");
        }
        return Result.success(order);
    }
}