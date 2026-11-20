package com.mntech.shopadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mntech.shopadmin.dto.response.ApiResponse;
import com.mntech.shopadmin.entity.PointsAccountModel;
import com.mntech.shopadmin.entity.PointsRecordModel;
import com.mntech.shopadmin.service.IPointsAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/points/admin")
public class PointsAdminController {

    @Autowired
    private IPointsAdminService pointsAdminService;

    /**
     * 积分账户分页（昵称/用户ID 关键字过滤）
     */
    @GetMapping("/accounts")
    public ApiResponse<Map<String, Object>> listAccounts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        IPage<PointsAccountModel> result = pointsAdminService.listAccounts(page, size, keyword);
        Map<String, Object> response = new HashMap<>();
        response.put("list", result.getRecords());
        response.put("total", result.getTotal());
        response.put("page", result.getCurrent());
        response.put("pageSize", result.getSize());
        response.put("totalPages", result.getPages());
        return ApiResponse.success("查询成功", response);
    }

    /**
     * 积分流水分页（用户ID/变动类型 过滤）
     */
    @GetMapping("/records")
    public ApiResponse<Map<String, Object>> listRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String changeType) {
        IPage<PointsRecordModel> result = pointsAdminService.listRecords(page, size, userId, changeType);
        Map<String, Object> response = new HashMap<>();
        response.put("list", result.getRecords());
        response.put("total", result.getTotal());
        response.put("page", result.getCurrent());
        response.put("pageSize", result.getSize());
        response.put("totalPages", result.getPages());
        return ApiResponse.success("查询成功", response);
    }
}