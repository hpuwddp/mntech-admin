package com.mntech.shopadmin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mntech.shopadmin.dto.DistributionShareSetting;
import com.mntech.shopadmin.entity.CommissionRecordModel;
import com.mntech.shopadmin.entity.DistributionConfigModel;
import com.mntech.shopadmin.entity.DistributionWithdrawalModel;

import java.util.List;
import java.util.Map;

/**
 * 分销管理服务（管理端）
 */
public interface IDistributionAdminService {

    // ==================== 分销配置 ====================

    List<DistributionConfigModel> listConfigs();

    void updateConfigs(List<DistributionConfigModel> configs);

    // ==================== 分享与海报配置 ====================

    /**
     * 分享/海报/微信相关平台配置（key-value，按 key 返回）
     */
    Map<String, String> listShareSettings();

    /**
     * 保存分享/海报/微信相关平台配置（存在则更新，新增则插入）
     */
    void saveShareSettings(List<DistributionShareSetting> settings);

    // ==================== 佣金记录 ====================

    IPage<CommissionRecordModel> listCommissions(Integer page, Integer size, String orderNo, String userId, String status);

    // ==================== 提现审核 ====================

    IPage<DistributionWithdrawalModel> listWithdrawals(Integer page, Integer size, String status, String userId);

    void reviewWithdrawal(String id, String action, String reviewer, String remark);

    /**
     * 打款失败后重试打款（仅 APPROVED + payout_status=FAILED 可重试）
     */
    void repayWithdrawal(String id, String operator);

    // ==================== 统计 ====================

    Map<String, Object> getStats();
}