package com.mntech.shopadmin.service.impl;

import java.math.BigDecimal;

/**
 * 打款渠道抽象（扩展点）：
 * 默认提供 {@link MockPayoutChannel}（不真正转账，仅生成 MOCK 前缀流水号）。
 * 接入真实渠道（微信商家转账 / 银行代付）时：
 * 1. 新写一个实现类，用真实 API 发起转账并返回渠道流水号；
 * 2. 打款失败时抛出 {@link PayoutException}（携带失败原因），由审核/重试流程把记录置为 FAILED 等待重试；
 * 3. 真实渠道注意幂等：可在打款前查询渠道侧是否已成功，避免重复转账。
 */
public interface PayoutChannel {

    /**
     * 发起打款
     *
     * @param withdrawalId 提现单 ID
     * @param userId       收款用户 ID
     * @param amount       打款金额
     * @return 渠道流水号
     * @throws PayoutException 打款失败（携带原因）
     */
    String pay(String withdrawalId, String userId, BigDecimal amount) throws PayoutException;

    /**
     * 渠道名称，用于日志与流水号前缀
     */
    String channelName();
}