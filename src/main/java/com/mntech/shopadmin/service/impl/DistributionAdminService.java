package com.mntech.shopadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mntech.shopadmin.dao.CommissionRecordMapper;
import com.mntech.shopadmin.dao.DistributionConfigMapper;
import com.mntech.shopadmin.dao.DistributionRelationMapper;
import com.mntech.shopadmin.dao.DistributionWithdrawalMapper;
import com.mntech.shopadmin.dao.ShopConfigMapper;
import com.mntech.shopadmin.dto.DistributionShareSetting;
import com.mntech.shopadmin.entity.CommissionRecordModel;
import com.mntech.shopadmin.entity.DistributionConfigModel;
import com.mntech.shopadmin.entity.DistributionWithdrawalModel;
import com.mntech.shopadmin.entity.ShopConfigModel;
import com.mntech.shopadmin.exception.BusinessException;
import com.mntech.shopadmin.service.IDistributionAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 分销管理服务实现（管理端）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DistributionAdminService implements IDistributionAdminService {

    private static final BigDecimal MAX_RATE = new BigDecimal("0.5000");

    /** 分享/海报/微信配置白名单（顺序即界面展示顺序） */
    private static final String[] SHARE_KEYS = {
            "wechat.appid", "wechat.secret",
            "poster.shopName", "poster.qrPage",
            "share.title", "share.desc", "share.imageUrl", "share.path"
    };

    /** 白名单 key 缺失时的默认值（与 add.sql 种子保持一致） */
    private static final Map<String, String> SHARE_DEFAULTS = new HashMap<String, String>() {{
        put("wechat.appid", "");
        put("wechat.secret", "");
        put("poster.shopName", "琼浆玉液官方旗舰店");
        put("poster.qrPage", "pages/index/index");
        put("share.title", "邀请你一起买好酒，分享赚佣金");
        put("share.desc", "好友下单，你享返利，快来一起选购吧");
        put("share.imageUrl", "");
        put("share.path", "pages/index/index");
    }};

    private final DistributionConfigMapper configMapper;
    private final CommissionRecordMapper commissionMapper;
    private final DistributionWithdrawalMapper withdrawalMapper;
    private final DistributionRelationMapper relationMapper;
    private final PayoutChannel payoutChannel;
    private final ShopConfigMapper shopConfigMapper;

    // ==================== 分销配置 ====================

    @Override
    public List<DistributionConfigModel> listConfigs() {
        LambdaQueryWrapper<DistributionConfigModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(DistributionConfigModel::getLevel);
        return configMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void updateConfigs(List<DistributionConfigModel> configs) {
        if (configs == null || configs.isEmpty()) {
            throw new BusinessException("配置参数不能为空");
        }
        for (DistributionConfigModel cfg : configs) {
            Integer level = cfg.getLevel();
            BigDecimal rate = cfg.getRate();
            Integer enabled = cfg.getEnabled();
            if (level == null || level < 1 || level > 3) {
                throw new BusinessException("分销层级只能为 1/2/3");
            }
            if (rate == null || rate.compareTo(BigDecimal.ZERO) < 0 || rate.compareTo(MAX_RATE) > 0) {
                throw new BusinessException("第" + level + "级佣金比例需在 0 ~ 50% 之间");
            }
            if (enabled == null || (enabled != 0 && enabled != 1)) {
                throw new BusinessException("启用状态只能为 0 或 1");
            }

            LambdaQueryWrapper<DistributionConfigModel> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DistributionConfigModel::getLevel, level);
            DistributionConfigModel exist = configMapper.selectOne(wrapper);
            if (exist == null) {
                DistributionConfigModel insert = new DistributionConfigModel();
                insert.setLevel(level);
                insert.setRate(rate);
                insert.setEnabled(enabled);
                configMapper.insert(insert);
            } else {
                exist.setRate(rate);
                exist.setEnabled(enabled);
                configMapper.updateById(exist);
            }
        }
        log.info("更新分销配置成功: {} 条", configs.size());
    }

    // ==================== 分享与海报配置 ====================

    @Override
    public Map<String, String> listShareSettings() {
        Map<String, String> result = new LinkedHashMap<>();
        Map<String, ShopConfigModel> existMap = new HashMap<>();
        List<ShopConfigModel> list = shopConfigMapper.selectList(null);
        for (ShopConfigModel cfg : list) {
            existMap.put(cfg.getConfigKey(), cfg);
        }
        for (String key : SHARE_KEYS) {
            ShopConfigModel cfg = existMap.get(key);
            String value = cfg == null ? SHARE_DEFAULTS.getOrDefault(key, "") : cfg.getConfigValue();
            result.put(key, value == null ? "" : value);
        }
        return result;
    }

    @Override
    @Transactional
    public void saveShareSettings(List<DistributionShareSetting> settings) {
        if (settings == null || settings.isEmpty()) {
            throw new BusinessException("配置参数不能为空");
        }
        for (DistributionShareSetting setting : settings) {
            String key = setting.getConfigKey();
            if (!StringUtils.hasText(key)) {
                throw new BusinessException("配置键不能为空");
            }
            boolean allowed = false;
            for (String white : SHARE_KEYS) {
                if (white.equals(key)) {
                    allowed = true;
                    break;
                }
            }
            if (!allowed) {
                throw new BusinessException("不支持的配置键: " + key);
            }
            String value = setting.getConfigValue();
            if (value == null) {
                value = "";
            }

            LambdaQueryWrapper<ShopConfigModel> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ShopConfigModel::getConfigKey, key);
            ShopConfigModel exist = shopConfigMapper.selectOne(wrapper);
            if (exist == null) {
                ShopConfigModel insert = new ShopConfigModel();
                insert.setConfigKey(key);
                insert.setConfigValue(value);
                insert.setRemark(setting.getRemark());
                shopConfigMapper.insert(insert);
            } else {
                exist.setConfigValue(value);
                if (StringUtils.hasText(setting.getRemark())) {
                    exist.setRemark(setting.getRemark());
                }
                shopConfigMapper.updateById(exist);
            }
        }
        log.info("保存分享/海报配置成功: {} 条", settings.size());
    }

    // ==================== 佣金记录 ====================

    @Override
    public IPage<CommissionRecordModel> listCommissions(Integer page, Integer size, String orderNo, String userId, String status) {
        Page<CommissionRecordModel> pageParam = new Page<>(page, size);
        return commissionMapper.selectPageWithJoin(pageParam, orderNo, userId, status);
    }

    // ==================== 提现审核 ====================

    @Override
    public IPage<DistributionWithdrawalModel> listWithdrawals(Integer page, Integer size, String status, String userId) {
        Page<DistributionWithdrawalModel> pageParam = new Page<>(page, size);
        return withdrawalMapper.selectPageWithJoin(pageParam, status, userId);
    }

    @Override
    @Transactional
    public void reviewWithdrawal(String id, String action, String reviewer, String remark) {
        DistributionWithdrawalModel withdrawal = withdrawalMapper.selectById(id);
        if (withdrawal == null) {
            throw new BusinessException("提现单不存在");
        }
        if (!"PENDING".equals(withdrawal.getStatus())) {
            throw new BusinessException("该提现单已处理，不能重复审核");
        }
        String reviewerName = (reviewer == null || reviewer.isEmpty()) ? "admin" : reviewer;

        if ("APPROVED".equals(action)) {
            withdrawal.setStatus("APPROVED");
            withdrawal.setReviewTime(LocalDateTime.now());
            withdrawal.setReviewer(reviewerName);
            withdrawal.setRemark(remark);
            withdrawalMapper.updateById(withdrawal);
            int updated = commissionMapper.markWithdrawnByWithdrawalId(id);
            log.info("提现审核通过: {} 金额 {} 元，关联佣金 {} 条已到账", id, withdrawal.getAmount(), updated);
            doPayout(withdrawal);
        } else if ("REJECTED".equals(action)) {
            withdrawal.setStatus("REJECTED");
            withdrawal.setReviewTime(LocalDateTime.now());
            withdrawal.setReviewer(reviewerName);
            withdrawal.setRemark(remark);
            withdrawalMapper.updateById(withdrawal);
            int updated = commissionMapper.unlockByWithdrawalId(id);
            log.info("提现审核拒绝: {} 金额 {} 元，关联佣金 {} 条已退回", id, withdrawal.getAmount(), updated);
        } else {
            throw new BusinessException("审核动作不合法，仅支持 APPROVED / REJECTED");
        }
    }

    /**
     * 打款失败后重试（仅 APPROVED + payout_status=FAILED 可重试）
     */
    @Override
    @Transactional
    public void repayWithdrawal(String id, String operator) {
        DistributionWithdrawalModel withdrawal = withdrawalMapper.selectById(id);
        if (withdrawal == null) {
            throw new BusinessException("提现单不存在");
        }
        if (!"APPROVED".equals(withdrawal.getStatus())) {
            throw new BusinessException("仅审核通过的提现单可重试打款");
        }
        if (!"FAILED".equals(withdrawal.getPayoutStatus())) {
            throw new BusinessException("仅打款失败的提现单可重试");
        }
        String operatorName = (operator == null || operator.isEmpty()) ? "admin" : operator;
        withdrawal.setRemark((withdrawal.getRemark() == null ? "" : withdrawal.getRemark()) + " [重试人:" + operatorName + "]");
        withdrawalMapper.updateById(withdrawal);
        doPayout(withdrawal);
        log.info("提现重试打款: {} 金额 {} 元", id, withdrawal.getAmount());
    }

    /**
     * 执行打款：成功写渠道流水号与时间，失败写原因并置 FAILED（可重试）
     */
    private void doPayout(DistributionWithdrawalModel withdrawal) {
        try {
            String payoutNo = payoutChannel.pay(withdrawal.getId(), withdrawal.getUserId(), withdrawal.getAmount());
            int updated = withdrawalMapper.markPayoutSuccess(withdrawal.getId(), payoutNo);
            log.info("打款成功: withdrawalId={} payoutNo={} 更新 {} 行", withdrawal.getId(), payoutNo, updated);
        } catch (PayoutException e) {
            withdrawalMapper.markPayoutFailed(withdrawal.getId(), e.getMessage());
            log.warn("打款失败: withdrawalId={} 原因={}", withdrawal.getId(), e.getMessage());
        }
    }

    // ==================== 统计 ====================

    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("relationCount", relationMapper.countAll());
        stats.put("pendingCommissionAmount", commissionMapper.sumAmountByStatus("PENDING"));
        stats.put("availableAmount", commissionMapper.sumAmountByStatus("AVAILABLE"));
        stats.put("lockingAmount", commissionMapper.sumAmountByStatus("LOCKED"));
        stats.put("withdrawnAmount", commissionMapper.sumAmountByStatus("WITHDRAWN"));
        stats.put("pendingWithdrawalCount", withdrawalMapper.countPending());
        stats.put("pendingWithdrawalAmount", withdrawalMapper.sumPendingAmount());
        return stats;
    }
}