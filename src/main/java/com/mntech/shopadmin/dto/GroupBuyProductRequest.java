package com.mntech.shopadmin.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class GroupBuyProductRequest {

    @NotBlank(message = "活动ID不能为空")
    private String activityId;

    @NotBlank(message = "商品ID不能为空")
    private String productId;

    private String skuId;

    @NotNull(message = "团购价不能为空")
    @Positive(message = "团购价必须大于0")
    private BigDecimal groupPrice;

    private BigDecimal originalPrice;

    @NotNull(message = "库存不能为空")
    @Positive(message = "库存必须大于0")
    private Integer stock;

    private Integer perUserLimit = 1;

    private Integer minPeople = 2;

    private Integer groupHours = 24;

    private Integer sortOrder = 0;
}