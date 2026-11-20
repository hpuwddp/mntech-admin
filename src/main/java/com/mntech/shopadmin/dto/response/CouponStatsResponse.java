package com.mntech.shopadmin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponStatsResponse {

    private Long totalCoupons;   // 总优惠券数
    private Long pending;        // 待审核
    private Long approved;       // 已通过
    private Long rejected;       // 已驳回
    private Long expired;        // 已过期
    private Long received;       // 已领取
    private Long used;           // 已使用
}