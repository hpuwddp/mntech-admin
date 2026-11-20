package com.mntech.shopadmin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupBuyTeamResponse {

    private String id;
    private String groupNo;
    private String activityId;
    private String activityName;
    private String groupBuyProductId;
    private String leaderUserId;
    private String leaderNickname;
    private String leaderAvatar;
    private Integer currentNum;
    private Integer targetNum;
    private String status;
    private String statusDesc;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;

    private String productName;
    private String productImage;

    private Long orderCount;
}