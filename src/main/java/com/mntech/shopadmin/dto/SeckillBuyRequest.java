package com.mntech.shopadmin.dto;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
@Data
public class SeckillBuyRequest {

    @NotBlank(message = "活动ID不能为空")
    private String activityId;

    @NotBlank(message = "秒杀商品ID不能为空")
    private String seckillProductId;

    @NotBlank(message = "用户ID不能为空")
    private String userId;

    @NotNull(message = "购买数量不能为空")
    @Positive(message = "购买数量必须大于0")
    private Integer quantity;
}
