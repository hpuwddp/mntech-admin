package com.mntech.shopadmin.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * 秒杀商品请求DTO
 */
@Data
public class SeckillProductRequest {

    /**
     * 活动ID
     */
    @NotBlank(message = "活动ID不能为空")
    private String activityId;

    /**
     * 商品ID
     */
    @NotBlank(message = "商品ID不能为空")
    private String productId;

    /**
     * SKU ID（可选）
     */
    private String skuId;

    /**
     * 秒杀价
     */
    @NotNull(message = "秒杀价不能为空")
    @Positive(message = "秒杀价必须大于0")
    private BigDecimal seckillPrice;

    /**
     * 秒杀库存
     */
    @NotNull(message = "秒杀库存不能为空")
    @Positive(message = "秒杀库存必须大于0")
    private Integer stock;

    /**
     * 每人限购数量（默认1）
     */
    private Integer perUserLimit = 1;

    /**
     * 排序
     */
    private Integer sortOrder = 0;
}