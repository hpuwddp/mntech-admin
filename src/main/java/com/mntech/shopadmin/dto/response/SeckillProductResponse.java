package com.mntech.shopadmin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 秒杀商品响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillProductResponse {

    // ==================== 秒杀商品信息 ====================

    private String id;
    private String activityId;
    private String productId;
    private String skuId;
    private BigDecimal seckillPrice;
    private Integer stock;
    private Integer remainingStock;
    private Integer perUserLimit;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ==================== 关联商品信息 ====================

    private String productName;
    private String productSubtitle;
    private String imageUrl;           // 商品主图
    private BigDecimal originalPrice;  // 商品原价
    private String skuName;            // SKU名称
    private String categoryName;       // 分类名称
    private String brandName;          // 品牌名称

    // ==================== 计算字段 ====================

    private BigDecimal discountAmount;  // 优惠金额
    private String discountDisplay;     // 折扣显示（如 "8折"）
    private Boolean isSoldOut;          // 是否售罄
    private Integer salesCount;         // 销量

    // ==================== 辅助方法 ====================

    public Boolean getIsSoldOut() {
        return remainingStock == null || remainingStock <= 0;
    }

    public BigDecimal getDiscountAmount() {
        if (originalPrice == null || seckillPrice == null) {
            return BigDecimal.ZERO;
        }
        return originalPrice.subtract(seckillPrice);
    }

    public String getDiscountDisplay() {
        if (originalPrice == null || seckillPrice == null || originalPrice.compareTo(BigDecimal.ZERO) == 0) {
            return "0折";
        }
        BigDecimal discount = seckillPrice.divide(originalPrice, 2, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.TEN);
        return discount.setScale(0, BigDecimal.ROUND_HALF_UP) + "折";
    }

    public String getSeckillPriceDisplay() {
        return seckillPrice != null ? "¥" + seckillPrice.setScale(2, BigDecimal.ROUND_HALF_UP) : "¥0.00";
    }

    public String getOriginalPriceDisplay() {
        return originalPrice != null ? "¥" + originalPrice.setScale(2, BigDecimal.ROUND_HALF_UP) : "¥0.00";
    }

    /**
     * 库存进度百分比
     */
    public Integer getStockPercent() {
        if (stock == null || stock == 0) return 0;
        Integer sold = stock - remainingStock;
        return (int) Math.round((double) sold / stock * 100);
    }
}