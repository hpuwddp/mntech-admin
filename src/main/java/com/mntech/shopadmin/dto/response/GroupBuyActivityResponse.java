package com.mntech.shopadmin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupBuyActivityResponse {

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

    private Integer productCount;

    private List<GroupBuyProductResponse> products;

    private Boolean isOngoing;
    private Boolean isEnded;
    private Boolean isPending;
}