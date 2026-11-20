package com.mntech.shopadmin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mntech.shopadmin.entity.PointsAccountModel;
import com.mntech.shopadmin.entity.PointsRecordModel;

/**
 * 积分管理服务（管理端）
 */
public interface IPointsAdminService {

    IPage<PointsAccountModel> listAccounts(Integer page, Integer size, String keyword);

    IPage<PointsRecordModel> listRecords(Integer page, Integer size, String userId, String changeType);
}