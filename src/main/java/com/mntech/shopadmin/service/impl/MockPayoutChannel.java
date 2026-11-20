package com.mntech.shopadmin.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * MOCK 打款渠道：不真正转账，生成 MOCK 前缀流水号。
 * 模拟上限：单笔金额大于等于 10000 元时抛出 {@link PayoutException}（便于管理端演示 FAILED 重试链路）。
 * 接入真实渠道时替换此实现即可，审核与重试流程无需改动。
 */
@Slf4j
@Component
public class MockPayoutChannel implements PayoutChannel {

    private static final BigDecimal FAIL_LIMIT = new BigDecimal("10000.0000");

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    @Override
    public String pay(String withdrawalId, String userId, BigDecimal amount) throws PayoutException {
        if (amount != null && amount.compareTo(FAIL_LIMIT) >= 0) {
            log.warn("MOCK 打款失败：金额 {} 超过模拟上限 {}", amount, FAIL_LIMIT);
            throw new PayoutException("MOCK 渠道模拟失败：单笔打款金额超过 " + FAIL_LIMIT.stripTrailingZeros().toPlainString() + " 元");
        }
        String suffix = withdrawalId == null ? "000000" : withdrawalId.substring(Math.max(0, withdrawalId.length() - 6));
        String payoutNo = "MOCK" + LocalDateTime.now().format(FMT) + suffix;
        log.info("MOCK 打款成功：withdrawalId={} userId={} amount={} payoutNo={}", withdrawalId, userId, amount, payoutNo);
        return payoutNo;
    }

    @Override
    public String channelName() {
        return "MOCK";
    }
}