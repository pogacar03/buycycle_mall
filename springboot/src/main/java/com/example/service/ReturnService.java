package com.example.service;

import com.example.entity.Return;
import com.example.mapper.ReturnMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReturnService {

    @Resource
    private ReturnMapper returnMapper;

    public void apply(Return returnOrder) {
        // 设置初始状态
        returnOrder.setStatus("pending"); // 或者使用之前设置的值
        returnMapper.insert(returnOrder);
    }

    public List<Return> getByUserId(Integer userId) {
        return returnMapper.selectByUserId(userId);
    }

    /**
     * 分页查询退货/换货申请列表（商家后台）
     * 
     * @param pageNum    页码
     * @param pageSize   每页大小
     * @param search     搜索条件（订单号）
     * @param status     状态筛选
     * @param businessId 商家ID
     * @return 结果
     */
    public Map<String, Object> selectPage(Integer pageNum, Integer pageSize, String search, String status,
            Integer businessId) {
        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;

        // 查询满足条件的总数
        Integer total = returnMapper.selectCountForBusiness(search, status, businessId);

        // 查询当前页数据
        List<Return> records = returnMapper.selectPageForBusiness(search, status, businessId, offset, pageSize);

        // 封装返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);

        return result;
    }

    /**
     * 根据ID获取退货申请
     * 
     * @param id 申请ID
     * @return 退货申请
     */
    public Return getById(Integer id) {
        return returnMapper.selectById(id);
    }

    /**
     * 更新退货申请
     * 
     * @param returnOrder 退货申请
     * @return 更新结果
     */
    public boolean update(Return returnOrder) {
        try {
            System.out.println("更新退货申请，ID: " + returnOrder.getId() + ", 状态: " + returnOrder.getStatus());
            int result = returnMapper.updateById(returnOrder);
            System.out.println("更新结果: " + result);
            return result > 0;
        } catch (Exception e) {
            System.out.println("更新退货申请失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 查询所有退货/换货申请（管理员用）
     */
    public Map<String, Object> selectAllPage(Integer pageNum, Integer pageSize, String search, String status) {
        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;

        // 查询满足条件的总数
        Integer total = returnMapper.selectCountAll(search, status);

        // 查询当前页数据
        List<Return> records = returnMapper.selectPageAll(search, status, offset, pageSize);

        // 封装返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);

        return result;
    }
}