package com.mntech.shopadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("user_coupon")
public class UserCouponModel {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("coupon_id")
    private String couponId;

    @TableField("user_id")
    private String userId;

    @TableField("order_no")
    private String orderNo;

    @TableField("status")
    private String status; // UNUSED(未使用), USED(已使用), EXPIRED(已过期)

    @TableField("start_time")
    private LocalDateTime startTime;

    @TableField("end_time")
    private LocalDateTime endTime;

    @TableField("used_time")
    private LocalDateTime usedTime;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    // ========== 关联字段（非表字段） ==========

    @TableField(exist = false)
    private String couponName;

    @TableField(exist = false)
    private String couponType;

    @TableField(exist = false)
    private BigDecimal discountValue;

    @TableField(exist = false)
    private BigDecimal minAmount;

    @TableField(exist = false)
    private BigDecimal maxDiscount;

    @TableField(exist = false)
    private String applicableScope;

    @TableField(exist = false)
    private String applicableIds;

    public String getStatusDesc() {
        if (status == null) return "";
        switch (status) {
            case "UNUSED": return "未使用";
            case "USED": return "已使用";
            case "EXPIRED": return "已过期";
            default: return status;
        }
    }
}