package com.mntech.shopadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("seckill_order")
public class SeckillOrderModel {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("activity_id")
    private String activityId;

    @TableField("seckill_product_id")
    private String seckillProductId;

    @TableField("user_id")
    private String userId;

    @TableField("order_no")
    private String orderNo;

    @TableField("quantity")
    private Integer quantity;

    @TableField("seckill_price")
    private BigDecimal seckillPrice;

    @TableField("status")
    private String status; // PENDING(待支付), PAID(已支付), CANCELLED(已取消)

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

@TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    @TableField("deleted")
    private Integer deleted; //逻辑删除: 0=未删除, 1=已删除
    // ========== 非表字段 ==========

    @TableField(exist = false)
    private String activityName;

    @TableField(exist = false)
    private String productName;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private String userNickname;

    @TableField(exist = false)
    private BigDecimal totalAmount;

    public String getStatusDesc() {
        if (status == null) return "";
switch (status) {
            case "PENDING": return "待支付";
            case "PAID": return "已支付";
            case "CANCELLED": return "已取消";
            case "REFUND":
            case "REFUNDED": return "已退款";
            default: return status;
        }
    }

    public BigDecimal getTotalAmount() {
        if (seckillPrice == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return seckillPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public Boolean getIsPending() {
        return "PENDING".equals(status);
    }

    public Boolean getIsPaid() {
        return "PAID".equals(status);
    }
}
