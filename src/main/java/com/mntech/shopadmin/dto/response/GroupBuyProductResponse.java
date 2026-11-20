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
public class GroupBuyProductResponse {

    private String id;
    private String activityId;
    private String productId;
    private String skuId;
    private BigDecimal groupPrice;
    private BigDecimal originalPrice;
    private Integer stock;
    private Integer remainingStock;
    private Integer perUserLimit;
    private Integer minPeople;
    private Integer groupHours;
    private Integer sortOrder;
    private Integer salesCount;
    private Integer lockedStock;
    private Integer soldStock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String productName;
    private String imageUrl;
    private Boolean isSoldOut;
    private String groupPriceDisplay;
    private String discountDisplay;
}