package com.mntech.shopadmin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mntech.shopadmin.entity.GroupBuyProductModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GroupBuyProductMapper extends BaseMapper<GroupBuyProductModel> {

    /**
     * 获取活动商品列表（关联商品信息）
     */
    @Select("SELECT gbp.*, p.name AS product_name, p.main_image AS image_url " +
            "FROM group_buy_product gbp " +
            "LEFT JOIN product p ON p.id = gbp.product_id " +
            "WHERE gbp.activity_id = #{activityId} AND gbp.deleted = 0 " +
            "ORDER BY gbp.sort_order ASC, gbp.created_at ASC")
    List<GroupBuyProductModel> selectByActivityIdWithProduct(@Param("activityId") String activityId);

    /**
     * 获取商品详情（关联商品信息）
     */
    @Select("SELECT gbp.*, p.name AS product_name, p.main_image AS image_url " +
            "FROM group_buy_product gbp " +
            "LEFT JOIN product p ON p.id = gbp.product_id " +
            "WHERE gbp.id = #{id} AND gbp.deleted = 0")
    GroupBuyProductModel selectByIdWithProduct(@Param("id") String id);
}