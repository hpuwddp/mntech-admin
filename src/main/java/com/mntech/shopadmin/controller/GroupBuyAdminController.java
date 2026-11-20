package com.mntech.shopadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mntech.shopadmin.dto.GroupBuyActivityRequest;
import com.mntech.shopadmin.dto.GroupBuyProductRequest;
import com.mntech.shopadmin.dto.response.ApiResponse;
import com.mntech.shopadmin.dto.response.GroupBuyActivityResponse;
import com.mntech.shopadmin.dto.response.GroupBuyOrderResponse;
import com.mntech.shopadmin.dto.response.GroupBuyProductResponse;
import com.mntech.shopadmin.entity.GroupBuyActivityModel;
import com.mntech.shopadmin.entity.GroupBuyOrderModel;
import com.mntech.shopadmin.entity.GroupBuyTeamModel;
import com.mntech.shopadmin.service.IGroupBuyAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groupbuy/admin")
public class GroupBuyAdminController {

    @Autowired
    private IGroupBuyAdminService groupBuyAdminService;

    // ==================== 活动管理 ====================

    /**
     * 创建团购活动
     */
    @PostMapping("/activity")
    public ApiResponse<GroupBuyActivityResponse> createActivity(@Valid @RequestBody GroupBuyActivityRequest request) {
        GroupBuyActivityResponse response = groupBuyAdminService.createActivity(request);
        return ApiResponse.success("创建成功", response);
    }

    /**
     * 获取活动详情
     */
    @GetMapping("/activity/{id}")
    public ApiResponse<GroupBuyActivityResponse> getActivityDetail(@PathVariable String id) {
        GroupBuyActivityResponse activity = groupBuyAdminService.getActivityDetail(id);
        return ApiResponse.success("查询成功", activity);
    }

    /**
     * 更新团购活动
     */
    @PutMapping("/activity/{id}")
    public ApiResponse<GroupBuyActivityResponse> updateActivity(@PathVariable String id, @Valid @RequestBody GroupBuyActivityRequest request) {
        GroupBuyActivityResponse response = groupBuyAdminService.updateActivity(id, request);
        return ApiResponse.success("更新成功", response);
    }

    /**
     * 审核团购活动
     */
    @PutMapping("/activity/{id}/audit")
    public ApiResponse<Void> auditActivity(@PathVariable String id, @RequestParam String status) {
        groupBuyAdminService.auditActivity(id, status);
        return ApiResponse.success("审核成功", null);
    }

    /**
     * 删除团购活动
     */
    @DeleteMapping("/activity/{id}")
    public ApiResponse<Void> deleteActivity(@PathVariable String id) {
        groupBuyAdminService.deleteActivity(id);
        return ApiResponse.success("删除成功", null);
    }

    /**
     * 团购活动列表
     */
    @GetMapping("/activity/list")
    public ApiResponse<Map<String, Object>> listActivities(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status) {
        IPage<GroupBuyActivityModel> result = groupBuyAdminService.listActivities(page, size, name, status);
        Map<String, Object> response = new HashMap<>();
        response.put("list", result.getRecords());
        response.put("total", result.getTotal());
        response.put("page", result.getCurrent());
        response.put("pageSize", result.getSize());
        response.put("totalPages", result.getPages());
        return ApiResponse.success("查询成功", response);
    }

    // ==================== 商品管理 ====================

    /**
     * 获取活动商品列表
     */
    @GetMapping("/product/list")
    public ApiResponse<List<GroupBuyProductResponse>> getActivityProducts(@RequestParam String activityId) {
        List<GroupBuyProductResponse> products = groupBuyAdminService.getActivityProducts(activityId);
        return ApiResponse.success("查询成功", products);
    }

    /**
     * 获取团购商品详情
     */
    @GetMapping("/product/{id}")
    public ApiResponse<GroupBuyProductResponse> getProductDetail(@PathVariable String id) {
        GroupBuyProductResponse product = groupBuyAdminService.getProductDetail(id);
        return ApiResponse.success("查询成功", product);
    }

    /**
     * 添加团购商品
     */
    @PostMapping("/product")
    public ApiResponse<Void> addProduct(@Valid @RequestBody GroupBuyProductRequest request) {
        groupBuyAdminService.addProduct(request);
        return ApiResponse.success("添加成功", null);
    }

    /**
     * 修改团购商品
     */
    @PutMapping("/product/{id}")
    public ApiResponse<Void> updateProduct(@PathVariable String id, @Valid @RequestBody GroupBuyProductRequest request) {
        groupBuyAdminService.updateProduct(id, request);
        return ApiResponse.success("修改成功", null);
    }

    /**
     * 删除团购商品
     */
    @DeleteMapping("/product/{id}")
    public ApiResponse<Void> removeProduct(@PathVariable String id) {
        groupBuyAdminService.removeProduct(id);
        return ApiResponse.success("删除成功", null);
    }

    // ==================== 团记录 ====================

    /**
     * 团记录分页列表
     */
    @GetMapping("/team/list")
    public ApiResponse<Map<String, Object>> listTeams(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String groupNo,
            @RequestParam(required = false) String activityId,
            @RequestParam(required = false) String status) {
        IPage<GroupBuyTeamModel> result = groupBuyAdminService.listTeams(page, size, groupNo, activityId, status);
        Map<String, Object> response = new HashMap<>();
        response.put("list", result.getRecords());
        response.put("total", result.getTotal());
        response.put("page", result.getCurrent());
        response.put("pageSize", result.getSize());
        response.put("totalPages", result.getPages());
        return ApiResponse.success("查询成功", response);
    }

    // ==================== 订单 ====================

    /**
     * 团购订单分页列表
     */
    @GetMapping("/order/list")
    public ApiResponse<Map<String, Object>> listOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String status) {
        IPage<GroupBuyOrderModel> result = groupBuyAdminService.listOrders(page, size, orderNo, userId, status);
        Map<String, Object> response = new HashMap<>();
        response.put("list", result.getRecords());
        response.put("total", result.getTotal());
        response.put("page", result.getCurrent());
        response.put("pageSize", result.getSize());
        response.put("totalPages", result.getPages());
        return ApiResponse.success("查询成功", response);
    }

    /**
     * 团购订单详情
     */
    @GetMapping("/order/{orderId}")
    public ApiResponse<GroupBuyOrderResponse> getOrderDetail(@PathVariable String orderId) {
        GroupBuyOrderResponse order = groupBuyAdminService.getOrderDetail(orderId);
        return ApiResponse.success("查询成功", order);
    }
}