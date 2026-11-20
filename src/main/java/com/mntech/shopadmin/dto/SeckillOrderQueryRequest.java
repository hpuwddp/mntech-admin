package com.mntech.shopadmin.dto;

import lombok.Data;

@Data
public class SeckillOrderQueryRequest {
    private String orderNo;
    private String userId;
    private String status;
    private Integer page = 1;
    private Integer size = 10;
}