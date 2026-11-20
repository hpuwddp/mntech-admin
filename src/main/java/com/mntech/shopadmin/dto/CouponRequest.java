package com.mntech.shopadmin.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CouponRequest {

    @NotBlank(message = "优惠券名称不能为空")
    private String name;

    private String description;

    @NotBlank(message = "优惠券类型不能为空")
    private String type; // cash, discount, free_shipping

    @NotNull(message = "优惠值不能为空")
    @Positive(message = "优惠值必须大于0")
    private BigDecimal discountValue;

    private BigDecimal minAmount;

    private BigDecimal maxDiscount;

    @NotNull(message = "发行总量不能为空")
    @Positive(message = "发行总量必须大于0")
    private Integer totalQuantity;

    private Integer perUserLimit = 1;

    @NotNull(message = "有效期开始时间不能为空")
    private LocalDateTime startTime;

    @NotNull(message = "有效期结束时间不能为空")
    private LocalDateTime endTime;

    private String applicableScope = "ALL";

    private String applicableIds;
}