package com.mntech.shopadmin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SeckillStatusEnum {

    PENDING("PENDING", "待审核"),
    APPROVED("APPROVED", "已通过"),
    REJECTED("REJECTED", "已驳回"),
    ONGOING("ONGOING", "进行中"),
    ENDED("ENDED", "已结束");

    private final String code;
    private final String desc;

    public static SeckillStatusEnum fromCode(String code) {
        for (SeckillStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}