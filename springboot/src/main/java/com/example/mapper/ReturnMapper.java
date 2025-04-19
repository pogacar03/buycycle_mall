package com.example.mapper;

import com.example.entity.Return;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ReturnMapper {
        /**
         * 插入退货申请
         * 
         * @param returnOrder 退货申请
         * @return 影响的行数
         */
        int insert(Return returnOrder);

        /**
         * 根据用户ID查询退货申请
         * 
         * @param userId 用户ID
         * @return 退货申请列表
         */
        List<Return> selectByUserId(@Param("userId") Integer userId);

        /**
         * 根据商家ID查询退货申请数量
         * 
         * @param search     搜索条件
         * @param status     状态
         * @param businessId 商家ID
         * @return 退货申请数量
         */
        Integer selectCountForBusiness(@Param("search") String search,
                        @Param("status") String status,
                        @Param("businessId") Integer businessId);

        /**
         * 分页查询商家的退货申请
         * 
         * @param search     搜索条件
         * @param status     状态
         * @param businessId 商家ID
         * @param offset     偏移量
         * @param pageSize   每页大小
         * @return 退货申请列表
         */
        List<Return> selectPageForBusiness(@Param("search") String search,
                        @Param("status") String status,
                        @Param("businessId") Integer businessId,
                        @Param("offset") Integer offset,
                        @Param("pageSize") Integer pageSize);

        /**
         * 根据ID查询退货申请
         * 
         * @param id 申请ID
         * @return 退货申请
         */
        Return selectById(@Param("id") Integer id);

        /**
         * 更新退货申请
         * 
         * @param returnOrder 退货申请
         * @return 影响的行数
         */
        int updateById(Return returnOrder);

        Integer selectCountAll(@Param("search") String search, @Param("status") String status);

        List<Return> selectPageAll(@Param("search") String search, @Param("status") String status,
                        @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);
}