package com.mntech.shopadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mntech.shopadmin.dto.CouponRequest;
import com.mntech.shopadmin.dto.response.ApiResponse;
import com.mntech.shopadmin.dto.response.CouponResponse;
import com.mntech.shopadmin.entity.CouponModel;
import com.mntech.shopadmin.service.ICouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/coupon")
public class CouponController {
    @Autowired
    private ICouponService couponService;

    // ==================== 管理端 ====================

    /**
     * 创建优惠券
     */
    @PostMapping("/admin/create")
    public ApiResponse<CouponResponse> createCoupon(@Valid @RequestBody CouponRequest request) {
        CouponResponse couponResponse = couponService.createCoupon(request);
        return ApiResponse.success("创建成功", couponResponse);
    }

    /**
     * 更新优惠券
     */
    @PutMapping("/admin/{id}")
    public ApiResponse<CouponResponse> updateCoupon(@PathVariable String id, @Valid @RequestBody CouponRequest request) {
        CouponResponse couponResponse = couponService.updateCoupon(id, request);
        return ApiResponse.success("更新成功", couponResponse);
    }

    /**
     * 删除优惠券
     */
    @DeleteMapping("/admin/{id}")
    public ApiResponse<Void> deleteCoupon(@PathVariable String id) {
        couponService.deleteCoupon(id);
        return ApiResponse.success("删除成功", null);
    }

    /**
     * 优惠券列表（管理端）
     */
    @GetMapping("/admin/list")
    public ApiResponse<Map<String, Object>> listCoupons(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status) {

        IPage<CouponModel> couponModelIPage = couponService.listCoupons(page, size, name, status);
        Map<String, Object> response = new HashMap<>();
        response.put("list", couponModelIPage.getRecords());
        response.put("total", couponModelIPage.getTotal());
        response.put("page", couponModelIPage.getCurrent());
        response.put("pageSize", couponModelIPage.getSize());
        response.put("totalPages", couponModelIPage.getPages());

        return ApiResponse.success("获取优惠券列表成功", response);
    }

    /**
     * 优惠券上下架
     */
    @PutMapping("/admin/{id}/status")
    public ApiResponse<CouponResponse> changeStatus(@PathVariable String id, @RequestParam String status) {
        CouponResponse couponResponse = couponService.changeStatus(id, status);
        return ApiResponse.success("操作成功", couponResponse);
    }


}