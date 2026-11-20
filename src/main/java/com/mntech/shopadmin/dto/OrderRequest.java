package com.mntech.shopadmin.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequest {
    private String orderNo;
    private String userId;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private String status;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String payType;
    private String remark;
    private List<OrderItemRequest> items;
}

@Data
class OrderItemRequest {
    private String productId;
    private String skuId;
    private String productName;
    private String skuName;
    private String productImage;
    private Integer quantity;
    private BigDecimal price;
}