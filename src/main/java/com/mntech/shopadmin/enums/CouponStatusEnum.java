package com.mntech.shopadmin.enums;
import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public enum CouponStatusEnum {

    ACTIVE("ACTIVE", "上架中"),
    INACTIVE("INACTIVE", "已下架"),
    EXPIRED("EXPIRED", "已过期");

    private final String code;
    private final String desc;

    public static CouponStatusEnum fromCode(String code) {
        for (CouponStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}