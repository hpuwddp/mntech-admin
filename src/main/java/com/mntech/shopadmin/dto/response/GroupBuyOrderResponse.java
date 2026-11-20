package com.mntech.shopadmin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupBuyOrderResponse {

    private String id;
    private String orderNo;
    private String teamId;
    private String groupNo;
    private String activityId;
    private String activityName;
    private String groupBuyProductId;
    private String userId;
    private String buyerNickname;
    private String buyerAvatar;
    private Integer quantity;
    private BigDecimal groupPrice;
    private BigDecimal totalAmount;
    private String status;
    private String statusDesc;
    private LocalDateTime payTime;
    private String transactionId;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private LocalDateTime createdAt;

    private String productName;
    private String productImage;

    private String teamStatus;
}