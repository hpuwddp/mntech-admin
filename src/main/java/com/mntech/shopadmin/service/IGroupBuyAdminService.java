package com.mntech.shopadmin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mntech.shopadmin.dto.GroupBuyActivityRequest;
import com.mntech.shopadmin.dto.GroupBuyProductRequest;
import com.mntech.shopadmin.dto.response.GroupBuyActivityResponse;
import com.mntech.shopadmin.dto.response.GroupBuyOrderResponse;
import com.mntech.shopadmin.dto.response.GroupBuyProductResponse;
import com.mntech.shopadmin.dto.response.GroupBuyTeamResponse;
import com.mntech.shopadmin.entity.GroupBuyActivityModel;
import com.mntech.shopadmin.entity.GroupBuyOrderModel;
import com.mntech.shopadmin.entity.GroupBuyTeamModel;

import java.util.List;

public interface IGroupBuyAdminService {

    // ==================== 活动管理 ====================

    GroupBuyActivityResponse createActivity(GroupBuyActivityRequest request);

    GroupBuyActivityResponse updateActivity(String id, GroupBuyActivityRequest request);

    void deleteActivity(String id);

    GroupBuyActivityResponse getActivityDetail(String id);

    IPage<GroupBuyActivityModel> listActivities(Integer page, Integer size, String name, String status);

    void auditActivity(String id, String status);

    // ==================== 商品管理 ====================

    void addProduct(GroupBuyProductRequest request);

    void updateProduct(String id, GroupBuyProductRequest request);

    void removeProduct(String id);

    List<GroupBuyProductResponse> getActivityProducts(String activityId);

    GroupBuyProductResponse getProductDetail(String id);

    // ==================== 团记录 ====================

    IPage<GroupBuyTeamModel> listTeams(Integer page, Integer size, String groupNo, String activityId, String status);

    // ==================== 订单 ====================

    IPage<GroupBuyOrderModel> listOrders(Integer page, Integer size, String orderNo, String userId, String status);

    GroupBuyOrderResponse getOrderDetail(String orderId);
}