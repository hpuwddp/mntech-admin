package com.mntech.shopadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mntech.shopadmin.dto.DistributionShareSetting;
import com.mntech.shopadmin.dto.response.ApiResponse;
import com.mntech.shopadmin.entity.CommissionRecordModel;
import com.mntech.shopadmin.entity.DistributionConfigModel;
import com.mntech.shopadmin.entity.DistributionWithdrawalModel;
import com.mntech.shopadmin.service.IDistributionAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/distribution/admin")
public class DistributionAdminController {

    @Autowired
    private IDistributionAdminService distributionAdminService;

    // ==================== 分销配置 ====================

    /**
     * 获取三级佣金比例配置
     */
    @GetMapping("/config/list")
    public ApiResponse<List<DistributionConfigModel>> listConfigs() {
        List<DistributionConfigModel> configs = distributionAdminService.listConfigs();
        return ApiResponse.success("查询成功", configs);
    }

    /**
     * 更新佣金比例配置（批量）
     */
    @PutMapping("/config")
    public ApiResponse<Void> updateConfigs(@RequestBody List<DistributionConfigModel> configs) {
        distributionAdminService.updateConfigs(configs);
        return ApiResponse.success("更新成功", null);
    }

    // ==================== 分享与海报配置 ====================

    /**
     * 获取分享与海报配置（微信 appid/secret、店铺名、海报落地页、分享标题/描述/图片/落地页）
     */
    @GetMapping("/config/share-settings")
    public ApiResponse<Map<String, String>> listShareSettings() {
        return ApiResponse.success("查询成功", distributionAdminService.listShareSettings());
    }

    /**
     * 保存分享与海报配置（批量，支持部分更新）
     */
    @PutMapping("/config/share-settings")
    public ApiResponse<Void> saveShareSettings(@RequestBody List<DistributionShareSetting> settings) {
        distributionAdminService.saveShareSettings(settings);
        return ApiResponse.success("保存成功", null);
    }

    // ==================== 佣金记录 ====================

    /**
     * 佣金记录分页列表
     */
    @GetMapping("/commission/list")
    public ApiResponse<Map<String, Object>> listCommissions(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String status) {
        IPage<CommissionRecordModel> result = distributionAdminService.listCommissions(page, size, orderNo, userId, status);
        Map<String, Object> response = new HashMap<>();
        response.put("list", result.getRecords());
        response.put("total", result.getTotal());
        response.put("page", result.getCurrent());
        response.put("pageSize", result.getSize());
        response.put("totalPages", result.getPages());
        return ApiResponse.success("查询成功", response);
    }

    // ==================== 提现审核 ====================

    /**
     * 提现单分页列表
     */
    @GetMapping("/withdrawal/list")
    public ApiResponse<Map<String, Object>> listWithdrawals(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String userId) {
        IPage<DistributionWithdrawalModel> result = distributionAdminService.listWithdrawals(page, size, status, userId);
        Map<String, Object> response = new HashMap<>();
        response.put("list", result.getRecords());
        response.put("total", result.getTotal());
        response.put("page", result.getCurrent());
        response.put("pageSize", result.getSize());
        response.put("totalPages", result.getPages());
        return ApiResponse.success("查询成功", response);
    }

    /**
     * 提现审核：action=APPROVED 通过 / REJECTED 拒绝
     */
    @PutMapping("/withdrawal/{id}/review")
    public ApiResponse<Void> reviewWithdrawal(@PathVariable String id,
                                              @RequestParam String action,
                                              @RequestParam(required = false) String reviewer,
                                              @RequestParam(required = false) String remark) {
        distributionAdminService.reviewWithdrawal(id, action, reviewer, remark);
        return ApiResponse.success(action.equals("APPROVED") ? "审核通过" : "已拒绝", null);
    }

    /**
     * 打款失败重试（仅 FAILED 可重试）
     */
    @PostMapping("/withdrawal/{id}/repay")
    public ApiResponse<Void> repayWithdrawal(@PathVariable String id,
                                             @RequestParam(required = false) String operator) {
        distributionAdminService.repayWithdrawal(id, operator);
        return ApiResponse.success("重试打款成功", null);
    }

    // ==================== 统计 ====================

    /**
     * 分销总览统计
     */
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getStats() {
        Map<String, Object> stats = distributionAdminService.getStats();
        return ApiResponse.success("查询成功", stats);
    }
}