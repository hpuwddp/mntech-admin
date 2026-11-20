package com.mntech.shopadmin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SeckillOrderStatusEnum {

    PENDING("PENDING", "待支付"),
    PAID("PAID", "已支付"),
    CANCELLED("CANCELLED", "已取消");

    private final String code;
    private final String desc;

    public static SeckillOrderStatusEnum fromCode(String code) {
        for (SeckillOrderStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}