package com.mntech.shopadmin.dto;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
public class SkuUpdateRequest {

    private String id;
    private String skuName;
    private BigDecimal sellingPrice;
    private Integer totalStock;
    private String imageUrl;
}
