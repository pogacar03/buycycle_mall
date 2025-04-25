package com.example.controller;

import com.example.common.Result;
import com.example.entity.Orders;
import com.example.entity.Return;
import com.example.entity.User;
import com.example.service.GoodsService;
import com.example.service.ReturnService;
import com.example.mapper.OrdersMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/return")
public class ReturnController {

    @Resource
    private ReturnService returnService;

    @Resource
    private OrdersMapper ordersMapper;

    @Resource
    private GoodsService goodsService;

    @PostMapping("/apply")
    public Result apply(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            System.out.println("接收到退货/换货申请参数: " + params);

            // 从请求体中获取参数
            String orderId = (String) params.get("orderId");
            Integer goodsId = Integer.valueOf(params.get("goodsId").toString());
            String goodsName = (String) params.get("goodsName");
            String type = (String) params.get("type");
            String reason = (String) params.get("reason");
            String description = (String) params.get("description");

            // 参数校验
            if (orderId == null || goodsId == null || goodsName == null) {
                return Result.error("400", "订单信息不完整");
            }

            // 创建退货/换货订单
            Return returnOrder = new Return();
            returnOrder.setOrderId(orderId);
            returnOrder.setGoodsId(goodsId);
            returnOrder.setGoodsName(goodsName);
            returnOrder.setType(type);
            returnOrder.setReason(reason);
            returnOrder.setDescription(description);

            // 处理金额
            if (params.containsKey("amount") && params.get("amount") != null) {
                returnOrder.setAmount(new BigDecimal(params.get("amount").toString()));
            }

            returnOrder.setStatus("pending");

            // 先尝试从session获取用户ID
            Object userObj = request.getSession().getAttribute("user");

            // 如果session中没有，尝试从请求头中获取用户ID
            Integer userId = null;
            if (userObj == null) {
                // 检查前端可能发送的token
                String token = request.getHeader("token");
                if (token != null && !token.isEmpty()) {
                    // 如果有token验证逻辑，在这里处理
                    // ...

                    // 或者，临时从前端直接接收用户ID
                    Object userIdParam = params.get("userId");
                    if (userIdParam != null) {
                        userId = Integer.valueOf(userIdParam.toString());
                        returnOrder.setUserId(userId);
                    } else {
                        return Result.error("401", "用户未登录");
                    }
                } else {
                    return Result.error("401", "用户未登录");
                }
            } else {
                userId = ((User) userObj).getId();
                returnOrder.setUserId(userId);
            }

            // 保存退货/换货申请
            returnService.apply(returnOrder);

            // 更新订单的退货状态为"处理中"
            updateOrderReturnStatus(orderId, "1");

            return Result.success("申请提交成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("500", "系统异常: " + e.getMessage());
        }
    }

    // 更新订单的退货状态
    private void updateOrderReturnStatus(String orderId, String returnStatus) {
        // 根据 orderId 查询订单
        try {
            // 更新订单的 return_status 字段
            ordersMapper.updateReturnStatus(orderId, returnStatus);
        } catch (Exception e) {
            System.out.println("更新订单退货状态失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @GetMapping("/list")
    public Result list(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute("user");
        Integer userId = null;

        if (userObj == null) {
            // 尝试从请求参数获取用户ID
            String userIdStr = request.getParameter("userId");
            if (userIdStr != null && !userIdStr.isEmpty()) {
                userId = Integer.valueOf(userIdStr);
            } else {
                return Result.error("401", "用户未登录");
            }
        } else {
            userId = ((User) userObj).getId();
        }

        List<Return> returns = returnService.getByUserId(userId);
        return Result.success(returns);
    }

    /**
     * 分页查询退货/换货申请列表（商家后台）
     * 
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param search   搜索条件（订单号）
     * @param status   状态筛选
     * @param request  HTTP请求
     * @return 结果
     */
    @GetMapping("/page")
    public Result page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer businessId,
            HttpServletRequest request) {

        try {
            // 打印请求参数，帮助调试
            System.out.println("查询参数: pageNum=" + pageNum + ", pageSize=" + pageSize +
                    ", search=" + search + ", status=" + status + ", businessId=" + businessId);

            User user = null;
            String role = "BUSINESS"; // 默认角色
            // 先从session获取商家ID
            Object userObj = request.getSession().getAttribute("user");
            if (userObj != null) {
                if (userObj instanceof User) {
                    user = (User) userObj;
                    role = user.getRole();
                    System.out.println("从session获取到用户: " + user.getId() + ", 角色: " + role);
                } else if (userObj instanceof com.example.entity.Admin) {
                    role = "ADMIN";
                    System.out.println("从session获取到管理员用户");
                }
            } else {
                System.out.println("会话中没有用户信息");
                // 检查token中的角色信息
                String token = request.getHeader("token");
                if (token != null && !token.isEmpty()) {
                    // 从token中提取角色信息
                    // 这里假设有从token中提取角色的逻辑
                    role = "ADMIN"; // 临时假设为管理员
                }
            }

            // 对于管理员角色，查询所有退换货申请
            if ("ADMIN".equals(role)) {
                System.out.println("管理员查询所有退换货申请");
                Map<String, Object> result = returnService.selectAllPage(pageNum, pageSize, search, status);
                return Result.success(result);
            } else {
                // 非管理员用户，仍然按照商家ID查询
                if (businessId != null) {
                    System.out.println("使用参数传入的businessId: " + businessId);
                } else if (user != null) {
                    businessId = user.getId();
                    System.out.println("使用会话中的用户ID作为businessId: " + businessId);
                } else {
                    return Result.error("400", "缺少businessId参数");
                }

                Map<String, Object> result = returnService.selectPage(pageNum, pageSize, search, status, businessId);
                return Result.success(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("500", "系统异常: " + e.getMessage());
        }
    }

    /**
     * 审核通过退货/换货申请
     * 
     * @param id 申请ID
     * @return 结果
     */
    @PostMapping("/approve/{id}")
    public Result approve(@PathVariable Integer id, HttpServletRequest request) {
        try {
            System.out.println("收到审核通过请求，申请ID: " + id);

            // 从session获取用户对象
            Object userObj = request.getSession().getAttribute("user");

            // 获取请求中的商家ID参数
            String businessIdParam = request.getParameter("businessId");
            Integer businessId = null;

            // 使用 instanceof 检查用户对象类型
            if (userObj != null) {
                // 检查对象类型并获取ID
                if (userObj instanceof User) {
                    User user = (User) userObj;
                    businessId = user.getId();
                    System.out.println("使用User类型用户ID: " + businessId);
                } else if (userObj instanceof com.example.entity.Business) {
                    com.example.entity.Business business = (com.example.entity.Business) userObj;
                    businessId = business.getId();
                    System.out.println("使用Business类型用户ID: " + businessId);
                } else {
                    System.out.println("会话中的用户对象类型不支持: " + userObj.getClass().getName());
                }
            } else if (businessIdParam != null && !businessIdParam.isEmpty()) {
                // 如果请求参数中有商家ID，使用请求参数中的商家ID
                businessId = Integer.valueOf(businessIdParam);
                System.out.println("使用参数中的商家ID: " + businessId);
            } else {
                // 从请求头中获取token
                String token = request.getHeader("token");
                if (token == null || token.isEmpty()) {
                    System.out.println("未找到token或businessId，返回401");
                    return Result.error("401", "未登录或登录过期");
                }
            }

            // 获取退货申请详情
            Return returnOrder = returnService.getById(id);
            if (returnOrder == null) {
                return Result.error("404", "退货申请不存在");
            }

            // 更新退货申请状态为已通过
            returnOrder.setStatus("approved");
            boolean updated = returnService.update(returnOrder);

            if (updated) {
                return Result.success("操作成功");
            } else {
                return Result.error("500", "更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("500", "系统异常: " + e.getMessage());
        }
    }

    /**
     * 拒绝退货/换货申请
     * 
     * @param id 申请ID
     * @return 结果
     */
    @PostMapping("/reject/{id}")
    public Result reject(@PathVariable Integer id, HttpServletRequest request) {
        try {
            // 从session获取用户对象
            Object userObj = request.getSession().getAttribute("user");

            if (userObj == null) {
                String businessIdParam = request.getParameter("businessId");
                if (businessIdParam == null || businessIdParam.isEmpty()) {
                    return Result.error("401", "用户未登录");
                }
            }

            // 获取退货申请详情
            Return returnOrder = returnService.getById(id);
            if (returnOrder == null) {
                return Result.error("404", "退货申请不存在");
            }

            // 更新退货申请状态为已拒绝
            returnOrder.setStatus("rejected");
            boolean updated = returnService.update(returnOrder);

            if (updated) {
                // 更新订单的退货状态为原始状态（无退货）
                updateOrderReturnStatus(returnOrder.getOrderId(), "0");
                return Result.success("操作成功");
            } else {
                return Result.error("500", "更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("500", "系统异常: " + e.getMessage());
        }
    }

    /**
     * 完成退货/换货处理
     * 
     * @param id 申请ID
     * @return 结果
     */
    @PostMapping("/complete/{id}")
    public Result complete(@PathVariable Integer id, HttpServletRequest request) {
        try {
            // 从session获取用户对象
            Object userObj = request.getSession().getAttribute("user");

            if (userObj == null) {
                String businessIdParam = request.getParameter("businessId");
                if (businessIdParam == null || businessIdParam.isEmpty()) {
                    return Result.error("401", "用户未登录");
                }
            }

            // 获取退货申请详情
            Return returnOrder = returnService.getById(id);
            if (returnOrder == null) {
                return Result.error("404", "退货申请不存在");
            }

            // 更新退货申请状态为已完成
            returnOrder.setStatus("completed");
            boolean updated = returnService.update(returnOrder);

            if (updated) {
                // 更新订单的退货状态为已完成
                updateOrderReturnStatus(returnOrder.getOrderId(), "2");

                // 获取订单信息
                Orders order = ordersMapper.selectByOrderId(returnOrder.getOrderId());
                if (order != null) {
                    // 恢复商品库存
                    goodsService.updateStock(order.getGoodsId(), order.getNum());
                }

                return Result.success("操作成功");
            } else {
                return Result.error("500", "更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("500", "系统异常: " + e.getMessage());
        }
    }
}