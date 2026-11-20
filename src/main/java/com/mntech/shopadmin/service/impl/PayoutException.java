package com.mntech.shopadmin.service.impl;

/**
 * 打款渠道异常：携带失败原因，由审核/重试流程写入 fail_reason 并把打款状态置为 FAILED。
 */
public class PayoutException extends RuntimeException {

    public PayoutException(String message) {
        super(message);
    }

    public PayoutException(String message, Throwable cause) {
        super(message, cause);
    }
}