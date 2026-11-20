package com.mntech.shopadmin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mntech.shopadmin.dto.CouponRequest;
import com.mntech.shopadmin.dto.response.CouponResponse;
import com.mntech.shopadmin.entity.CouponModel;

public interface ICouponService {
    /**
     * 创建优惠券
     */
    CouponResponse createCoupon(CouponRequest request);

    /**
     * 更新优惠券
     */
    CouponResponse updateCoupon(String id, CouponRequest request);

    /**
     * 删除优惠券
     */
    void deleteCoupon(String id);

    /**
     * 分页查询优惠券列表
     */
    IPage<CouponModel> listCoupons(Integer page, Integer size, String name, String status);

    /**
     * 获取优惠券详情
     */
    CouponResponse getCouponDetail(String id);

    /**
     * 上下架优惠券（ACTIVE / INACTIVE）
     */
    CouponResponse changeStatus(String id, String status);
}