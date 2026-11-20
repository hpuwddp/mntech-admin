package com.mntech.shopadmin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mntech.shopadmin.entity.UserCouponModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserCouponMapper extends BaseMapper<UserCouponModel> {

    /**
     * 获取用户优惠券列表
     */
    @Select("SELECT uc.*, c.name, c.type, COALESCE(c.value, c.discount) AS discount_value, c.min_amount, c.max_discount, " +
            "c.applicable_scope, c.applicable_ids " +
            "FROM user_coupon uc " +
            "LEFT JOIN coupon c ON uc.coupon_id = c.id " +
            "WHERE uc.user_id = #{userId} " +
            "AND uc.deleted = 0 " +
            "AND c.deleted = 0 " +
            "ORDER BY uc.status = 'UNUSED' DESC, uc.end_time ASC")
    List<UserCouponModel> selectByUserId(@Param("userId") String userId);

    /**
     * 获取用户未使用的优惠券
     */
    @Select("SELECT uc.*, c.name, c.type, COALESCE(c.value, c.discount) AS discount_value, c.min_amount, c.max_discount " +
            "FROM user_coupon uc " +
            "LEFT JOIN coupon c ON uc.coupon_id = c.id " +
            "WHERE uc.user_id = #{userId} " +
            "AND uc.status = 'UNUSED' " +
            "AND uc.end_time >= NOW() " +
            "AND uc.deleted = 0 " +
            "AND c.deleted = 0")
    List<UserCouponModel> selectUnusedByUserId(@Param("userId") String userId);

    /**
     * 使用优惠券
     */
    @Update("UPDATE user_coupon SET status = 'USED', order_no = #{orderNo}, used_time = NOW() " +
            "WHERE id = #{id} AND status = 'UNUSED'")
    int useCoupon(@Param("id") String id, @Param("orderNo") String orderNo);

    /**
     * 回退优惠券（取消订单）
     */
    @Update("UPDATE user_coupon SET status = 'UNUSED', order_no = NULL, used_time = NULL " +
            "WHERE id = #{id} AND status = 'USED'")
    int rollbackCoupon(@Param("id") String id);

    /**
     * 更新过期状态
     */
    @Update("UPDATE user_coupon SET status = 'EXPIRED' " +
            "WHERE status = 'UNUSED' AND end_time < NOW()")
    int updateExpiredStatus();

    /**
     * 检查用户是否已领取
     */
    @Select("SELECT COUNT(*) FROM user_coupon " +
            "WHERE coupon_id = #{couponId} AND user_id = #{userId} AND status = 'UNUSED'")
    int countUserReceived(@Param("couponId") String couponId, @Param("userId") String userId);

    /**
     * 统计用户已使用数量
     */
    @Select("SELECT COUNT(*) FROM user_coupon WHERE coupon_id = #{couponId} AND user_id = #{userId} AND status = 'USED'")
    int countUserUsed(@Param("couponId") String couponId, @Param("userId") String userId);
}