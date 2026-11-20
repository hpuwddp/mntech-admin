package com.mntech.shopadmin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mntech.shopadmin.entity.SeckillProductModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SeckillProductMapper extends BaseMapper<SeckillProductModel> {

    /**
     * 获取活动商品列表
     */
    @Select("SELECT sp.*, p.name as product_name, p.main_image as image_url, " +
            "p.selling_price as original_price,s.sku_name AS sku_name " +
            "FROM seckill_product sp " +
            "LEFT JOIN product p ON sp.product_id = p.id " +
            "LEFT JOIN product_sku s ON sp.sku_id = s.id "+
            "WHERE sp.activity_id = #{activityId} " +
            "AND sp.deleted = 0 " +
            "AND p.deleted = 0 " +
            "ORDER BY sp.sort_order ASC, sp.created_at ASC")
    List<SeckillProductModel> selectByActivityId(@Param("activityId") String activityId);

    /**
     * 获取进行中的活动商品（用户端）
     */
    @Select("SELECT sp.*, p.name as product_name, p.main_image as image_url, " +
            "p.selling_price as original_price " +
            "FROM seckill_product sp " +
            "LEFT JOIN seckill_activity sa ON sp.activity_id = sa.id " +
            "LEFT JOIN product p ON sp.product_id = p.id " +
            "WHERE sa.status = 'ONGOING' " +
            "AND sp.remaining_stock > 0 " +
            "AND sp.deleted = 0 " +
            "AND p.deleted = 0 " +
            "ORDER BY sp.sort_order ASC")
    List<SeckillProductModel> selectOngoingProducts();

    /**
     * 扣减秒杀库存（原子操作）
     */
    @Update("UPDATE seckill_product SET remaining_stock = remaining_stock - #{quantity} " +
            "WHERE id = #{id} AND remaining_stock >= #{quantity}")
    int decreaseStock(@Param("id") String id, @Param("quantity") Integer quantity);

    /**
     * 增加秒杀库存（取消订单时回退）
     */
    @Update("UPDATE seckill_product SET remaining_stock = remaining_stock + #{quantity} " +
            "WHERE id = #{id}")
    int increaseStock(@Param("id") String id, @Param("quantity") Integer quantity);

    /**
     * 获取剩余库存
     */
    @Select("SELECT remaining_stock FROM seckill_product WHERE id = #{id}")
    Integer getRemainingStock(@Param("id") String id);

    /**
     * 获取秒杀价
     */
    @Select("SELECT seckill_price FROM seckill_product WHERE id = #{id}")
    java.math.BigDecimal getSeckillPrice(@Param("id") String id);

    /**
     * 获取限购数量
     */
    @Select("SELECT per_user_limit FROM seckill_product WHERE id = #{id}")
    Integer getPerUserLimit(@Param("id") String id);

    /**
     * 统计活动商品数
     */
    @Select("SELECT COUNT(*) FROM seckill_product WHERE activity_id = #{activityId} AND deleted = 0")
    int countByActivityId(@Param("activityId") String activityId);
}