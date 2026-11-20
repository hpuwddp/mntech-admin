package com.mntech.shopadmin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 秒杀活动响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillActivityResponse {

    // ==================== 活动基本信息 ====================

    private String id;
    private String name;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String statusDesc;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ==================== 统计信息 ====================

    private Integer productCount;           // 商品数量
    private Integer totalStock;             // 总库存
    private Integer totalRemainingStock;    // 总剩余库存
    private Integer totalSales;             // 总销量

    // ==================== 状态判断 ====================

    private Boolean isOngoing;              // 是否进行中
    private Boolean isEnded;                // 是否已结束
    private Boolean isPending;              // 是否待审核
    private Long remainingTime;             // 剩余时间（秒）

    // ==================== 关联数据 ====================

    private List<SeckillProductResponse> products;  // 商品列表

    // ==================== 辅助方法 ====================

    public String getStatusDesc() {
        if (status == null) return "";
        switch (status) {
            case "PENDING": return "待审核";
            case "APPROVED": return "已通过";
            case "REJECTED": return "已驳回";
            case "ONGOING": return "进行中";
            case "ENDED": return "已结束";
            default: return status;
        }
    }

    public Boolean getIsOngoing() {
        return "ONGOING".equals(status);
    }

    public Boolean getIsEnded() {
        return "ENDED".equals(status);
    }

    public Boolean getIsPending() {
        return "PENDING".equals(status);
    }

    public Long getRemainingTime() {
        if (startTime == null) return 0L;
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startTime)) {
            // 距离开始还有多久
            return java.time.Duration.between(now, startTime).getSeconds();
        } else if (now.isBefore(endTime)) {
            // 距离结束还有多久
            return java.time.Duration.between(now, endTime).getSeconds();
        }
        return 0L;
    }

    /**
     * 格式化剩余时间
     */
    public String getRemainingTimeDisplay() {
        Long seconds = getRemainingTime();
        if (seconds <= 0) return "已结束";

        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        if (days > 0) {
            return days + "天 " + hours + "时 " + minutes + "分";
        } else if (hours > 0) {
            return hours + "时 " + minutes + "分 " + secs + "秒";
        } else if (minutes > 0) {
            return minutes + "分 " + secs + "秒";
        } else {
            return secs + "秒";
        }
    }
}