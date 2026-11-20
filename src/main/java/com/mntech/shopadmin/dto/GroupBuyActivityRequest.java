package com.mntech.shopadmin.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class GroupBuyActivityRequest {

    @NotBlank(message = "活动名称不能为空")
    private String name;

    private String description;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    /**
     * 活动商品列表
     */
    private List<GroupBuyProductRequest> products;
}