package com.mntech.shopadmin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mntech.shopadmin.dao.PointsAccountMapper;
import com.mntech.shopadmin.dao.PointsRecordMapper;
import com.mntech.shopadmin.entity.PointsAccountModel;
import com.mntech.shopadmin.entity.PointsRecordModel;
import com.mntech.shopadmin.service.IPointsAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointsAdminServiceImpl implements IPointsAdminService {

    private final PointsAccountMapper accountMapper;
    private final PointsRecordMapper recordMapper;

    @Override
    public IPage<PointsAccountModel> listAccounts(Integer page, Integer size, String keyword) {
        Page<PointsAccountModel> pageParam = new Page<>(page, size);
        return accountMapper.selectAccountPage(pageParam, keyword);
    }

    @Override
    public IPage<PointsRecordModel> listRecords(Integer page, Integer size, String userId, String changeType) {
        Page<PointsRecordModel> pageParam = new Page<>(page, size);
        return recordMapper.selectRecordPage(pageParam, userId, changeType);
    }
}