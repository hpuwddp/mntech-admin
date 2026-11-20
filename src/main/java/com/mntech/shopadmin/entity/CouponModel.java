package com.mntech.shopadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("coupon")
public class CouponModel {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("name")
    private String name;

    @TableField("description")
    private String description;

    @TableField("type")
    private String type; // cash(满减), discount(折扣), free_shipping(免运费)

    @TableField("value")
    private BigDecimal value; // 满减金额

    @TableField("discount")
    private BigDecimal discount; // 折扣值（如 8.5 表示 85 折）

    @TableField("min_amount")
    private BigDecimal minAmount;

    @TableField("max_discount")
    private BigDecimal maxDiscount;

    @TableField("total_quantity")
    private Integer totalQuantity;

    @TableField("remaining_quantity")
    private Integer remainingQuantity;

    @TableField("user_limit")
    private Integer perUserLimit;

    @TableField("start_time")
    private LocalDateTime startTime;

    @TableField("end_time")
    private LocalDateTime endTime;

    @TableField("applicable_scope")
    private String applicableScope; // ALL, CATEGORY, PRODUCT

    @TableField("applicable_ids")
    private String applicableIds;

    @TableField("status")
    private String status; // ACTIVE(上架中), INACTIVE(已下架), EXPIRED(已过期)

    @TableField("created_by")
    private String createdBy;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    // ========== 非表字段（用于展示） ==========

    @TableField(exist = false)
    private String statusDesc;

    @TableField(exist = false)
    private String typeDesc;

    @TableField(exist = false)
    private BigDecimal discountValue;

    public String getStatusDesc() {
        if (status == null) return "";
        switch (status) {
            case "ACTIVE": return "上架中";
            case "INACTIVE": return "已下架";
            case "EXPIRED": return "已过期";
            default: return status;
        }
    }

    public String getTypeDesc() {
        if (type == null) return "";
        switch (type) {
            case "cash": return "满减券";
            case "discount": return "折扣券";
            case "free_shipping": return "免运费券";
            default: return type;
        }
    }

    public BigDecimal getDiscountValue() {
        if ("discount".equals(type)) {
            return discount;
        }
        return value;
    }
}