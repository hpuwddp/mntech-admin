package com.mntech.shopadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("group_buy_order")
public class GroupBuyOrderModel {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("order_no")
    private String orderNo;

    @TableField("team_id")
    private String teamId;

    @TableField("activity_id")
    private String activityId;

    @TableField("group_buy_product_id")
    private String groupBuyProductId;

    @TableField("user_id")
    private String userId;

    @TableField("quantity")
    private Integer quantity;

    @TableField("group_price")
    private BigDecimal groupPrice;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    @TableField("status")
    private String status; // PENDING(待支付), PAID(已支付), CANCELLED(已取消), REFUNDED(已退款)

    @TableField("pay_time")
    private LocalDateTime payTime;

    @TableField("transaction_id")
    private String transactionId;

    @TableField("receiver_name")
    private String receiverName;

    @TableField("receiver_phone")
    private String receiverPhone;

    @TableField("receiver_address")
    private String receiverAddress;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    // ========== 非表字段（关联查询） ==========

    @TableField(exist = false)
    private String productName;

    @TableField(exist = false)
    private String productImage;

    @TableField(exist = false)
    private String buyerNickname;

    @TableField(exist = false)
    private String buyerAvatar;

    @TableField(exist = false)
    private String groupNo;

    @TableField(exist = false)
    private String activityName;

    @TableField(exist = false)
    private String teamStatus;

    @TableField(exist = false)
    private String statusDesc;

    public String getStatusDesc() {
        if (status == null) return "";
        switch (status) {
            case "PENDING": return "待支付";
            case "PAID": return "已支付";
            case "CANCELLED": return "已取消";
            case "REFUNDED": return "已退款";
            default: return status;
        }
    }
}