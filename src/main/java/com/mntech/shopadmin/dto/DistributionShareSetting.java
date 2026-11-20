package com.mntech.shopadmin.dto;

import lombok.Data;

/**
 * 分享/海报/微信平台配置项（后台配置入参）
 */
@Data
public class DistributionShareSetting {

    /** 配置键，如 wechat.appid / poster.shopName / share.title */
    private String configKey;

    /** 配置值 */
    private String configValue;

    /** 备注 */
    private String remark;
}