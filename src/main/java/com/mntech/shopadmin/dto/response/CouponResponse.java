package com.mntech.shopadmin.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CouponResponse {

    private String id;
    private String name;
    private String description;
    private String type;
    private String typeDesc;
    private BigDecimal value;
    private BigDecimal discount;
    private BigDecimal discountValue;
    private BigDecimal minAmount;
    private BigDecimal maxDiscount;
    private Integer totalQuantity;
    private Integer remainingQuantity;
    private Integer perUserLimit;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String applicableScope;
    private String applicableIds;
    private String status;
    private String statusDesc;
    private LocalDateTime createdAt;

    public String getTypeDesc() {
        if (type == null) return "";
        switch (type) {
            case "cash": return "满减券";
            case "discount": return "折扣券";
            case "free_shipping": return "免运费券";
            default: return type;
        }
    }

    public String getStatusDesc() {
        if (status == null) return "";
        switch (status) {
            case "ACTIVE": return "上架中";
            case "INACTIVE": return "已下架";
            case "EXPIRED": return "已过期";
            default: return status;
        }
    }
}