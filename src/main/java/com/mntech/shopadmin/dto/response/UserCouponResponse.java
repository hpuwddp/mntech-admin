package com.mntech.shopadmin.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserCouponResponse {

    private String id;
    private String couponId;
    private String couponName;
    private String couponType;
    private String couponTypeDesc;
    private BigDecimal discountValue;
    private BigDecimal minAmount;
    private BigDecimal maxDiscount;
    private String status;
    private String statusDesc;
    private LocalDateTime endTime;
    private LocalDateTime receivedAt;
    private LocalDateTime usedTime;
    private String applicableScope;
    private String applicableIds;

    // 优惠券可用判断
    private Boolean isAvailable;
    private String displayValue;

    public String getDisplayValue() {
        if ("discount".equals(couponType)) {
            return discountValue == null ? "" : discountValue + "折";
        } else if ("cash".equals(couponType)) {
            return discountValue == null ? "" : "¥" + discountValue;
        } else if ("free_shipping".equals(couponType)) {
            return "免运费";
        }
        return "";
    }

    public Boolean getIsAvailable() {
        return "UNUSED".equals(status) && endTime != null && endTime.isAfter(LocalDateTime.now());
    }
}