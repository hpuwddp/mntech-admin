package com.mntech.shopadmin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mntech.shopadmin.entity.CouponModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CouponMapper extends BaseMapper<CouponModel> {

    /**
     * 获取用户可领取的优惠券列表
     */
    @Select("SELECT * FROM coupon WHERE status = 'ACTIVE' " +
            "AND remaining_quantity > 0 " +
            "AND start_time <= NOW() " +
            "AND end_time >= NOW() " +
            "AND deleted = 0 " +
            "ORDER BY created_at DESC")
    List<CouponModel> selectAvailableCoupons();

    /**
     * 获取用户可用优惠券（下单时）
     */
    @Select("SELECT c.* FROM coupon c " +
            "LEFT JOIN user_coupon uc ON uc.coupon_id = c.id AND uc.user_id = #{userId} AND uc.deleted = 0 " +
            "WHERE c.status = 'ACTIVE' " +
            "AND c.remaining_quantity > 0 " +
            "AND c.start_time <= NOW() " +
            "AND c.end_time >= NOW() " +
            "AND c.deleted = 0 " +
            "AND uc.id IS NULL " +
            "AND (c.applicable_scope = 'ALL' " +
            "     OR (c.applicable_scope = 'CATEGORY' AND #{categoryId} = ANY(string_to_array(c.applicable_ids, ','))) " +
            "     OR (c.applicable_scope = 'PRODUCT' AND #{productId} = ANY(string_to_array(c.applicable_ids, ','))))")
    List<CouponModel> selectAvailableForOrder(@Param("userId") String userId,
                                              @Param("categoryId") String categoryId,
                                              @Param("productId") String productId);

    /**
     * 减少优惠券库存
     */
    @Update("UPDATE coupon SET remaining_quantity = remaining_quantity - 1 " +
            "WHERE id = #{id} AND remaining_quantity > 0")
    int decreaseStock(@Param("id") String id);

    /**
     * 增加优惠券库存（取消订单时回退）
     */
    @Update("UPDATE coupon SET remaining_quantity = remaining_quantity + #{quantity} " +
            "WHERE id = #{id}")
    int increaseStock(@Param("id") String id, @Param("quantity") Integer quantity);

    /**
     * 更新过期状态
     */
    @Update("UPDATE coupon SET status = 'EXPIRED' " +
            "WHERE status IN ('ACTIVE', 'INACTIVE') " +
            "AND end_time < NOW()")
    int updateExpiredStatus();

    /**
     * 统计优惠券使用数量
     */
    @Select("SELECT COUNT(*) FROM user_coupon WHERE coupon_id = #{couponId} AND status = 'USED'")
    int countUsed(@Param("couponId") String couponId);
}