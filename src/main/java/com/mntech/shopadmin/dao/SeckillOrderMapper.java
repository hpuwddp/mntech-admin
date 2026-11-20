package com.mntech.shopadmin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mntech.shopadmin.entity.SeckillOrderModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SeckillOrderMapper extends BaseMapper<SeckillOrderModel> {

    /**
     * 获取用户秒杀订单
     */
    @Select("SELECT * FROM seckill_order " +
            "WHERE user_id = #{userId} " +
            "ORDER BY created_at DESC")
    List<SeckillOrderModel> selectByUserId(@Param("userId") String userId);

    /**
     * 获取用户对某个商品的购买数量
     */
    @Select("SELECT SUM(quantity) FROM seckill_order " +
            "WHERE user_id = #{userId} " +
            "AND seckill_product_id = #{seckillProductId} " +
            "AND status IN ('PENDING', 'PAID')")
    Integer getBuyCount(@Param("userId") String userId, @Param("seckillProductId") String seckillProductId);

    /**
     * 检查用户是否已购买（用于限购）
     */
    @Select("SELECT COUNT(*) FROM seckill_order " +
            "WHERE user_id = #{userId} " +
            "AND seckill_product_id = #{seckillProductId} " +
            "AND status IN ('PENDING', 'PAID')")
    int countUserBuy(@Param("userId") String userId, @Param("seckillProductId") String seckillProductId);

    /**
     * 更新订单状态为已支付
     */
    @Update("UPDATE seckill_order SET status = 'PAID', updated_at = NOW() " +
            "WHERE order_no = #{orderNo} AND status = 'PENDING'")
    int payOrder(@Param("orderNo") String orderNo);

    /**
     * 取消秒杀订单
     */
    @Update("UPDATE seckill_order SET status = 'CANCELLED', updated_at = NOW() " +
            "WHERE id = #{id} AND status = 'PENDING'")
    int cancelOrder(@Param("id") String id);

    /**
     * 查询待支付订单（超时取消）
     */
@Select("SELECT * FROM seckill_order " +
            "WHERE status = 'PENDING' " +
            "AND created_at < NOW() - INTERVAL '15 minutes'")
    List<SeckillOrderModel> selectPendingTimeoutOrders();
}