package com.mntech.shopadmin.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class ProductCreateRequest {
    @NotBlank(message = "产品名称不能为空")
    private String name;


    private String subtitle;


    private String description;

    @NotBlank(message = "分类ID不能为空")
    private String categoryId;



    @NotNull(message = "销售价不能为空")
    @Positive(message = "销售价必须大于0")
    private BigDecimal sellingPrice;


    private BigDecimal memberPrice;

    @PositiveOrZero(message = "库存不能为负数")
    private Integer totalStock;


    private Boolean hasSku;


    private Map<String, List<String>> skuTemplate;


    private BigDecimal weight;


   // private String shippingTemplateId;


    private String deliveryTime;


    private String mainImage;


    private List<String> images;


    private List<String> tags;


    private String createdBy;

    private List<SkuCreateRequest> skuCreateRequests;


}
