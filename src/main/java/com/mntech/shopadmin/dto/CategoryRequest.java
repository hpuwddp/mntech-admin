package com.mntech.shopadmin.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CategoryRequest {
    private String parentId;

    @NotBlank(message = "分类名称不能为空")
    private String name;

    private String code;
    private Integer level;
    private Integer sortOrder;
    private String icon;
    private String image;
    private String description;
    private String status;
}