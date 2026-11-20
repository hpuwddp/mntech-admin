package com.mntech.shopadmin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mntech.shopadmin.entity.GroupBuyActivityModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface GroupBuyActivityMapper extends BaseMapper<GroupBuyActivityModel> {

    /**
     * 统计活动商品数
     */
    @Select("SELECT COUNT(*) FROM group_buy_product WHERE activity_id = #{activityId} AND deleted = 0")
    int countProducts(@Param("activityId") String activityId);

    /**
     * 统计活动商品总库存
     */
    @Select("SELECT COALESCE(SUM(stock), 0) FROM group_buy_product WHERE activity_id = #{activityId} AND deleted = 0")
    int sumTotalStock(@Param("activityId") String activityId);

    /**
     * 统计活动商品剩余库存
     */
    @Select("SELECT COALESCE(SUM(remaining_stock), 0) FROM group_buy_product WHERE activity_id = #{activityId} AND deleted = 0")
    int sumRemainingStock(@Param("activityId") String activityId);
}