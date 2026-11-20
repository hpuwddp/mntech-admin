package com.mntech.shopadmin.enums;
import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public enum CouponTypeEnum {

    CASH("cash", "满减券"),
    DISCOUNT("discount", "折扣券"),
    FREE_SHIPPING("free_shipping", "免运费券");

    private final String code;
    private final String desc;

    public static CouponTypeEnum fromCode(String code) {
        for (CouponTypeEnum type : values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null;
    }
}