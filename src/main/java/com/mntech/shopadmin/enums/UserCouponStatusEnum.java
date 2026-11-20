package com.mntech.shopadmin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserCouponStatusEnum {

    UNUSED("UNUSED", "未使用"),
    USED("USED", "已使用"),
    EXPIRED("EXPIRED", "已过期");

    private final String code;
    private final String desc;

    public static UserCouponStatusEnum fromCode(String code) {
        for (UserCouponStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
