package com.mntech.shopadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mntech.shopadmin.dto.SeckillActivityRequest;
import com.mntech.shopadmin.dto.SeckillProductRequest;
import com.mntech.shopadmin.dto.response.ApiResponse;
import com.mntech.shopadmin.dto.response.SeckillActivityResponse;
import com.mntech.shopadmin.dto.response.SeckillProductResponse;
import com.mntech.shopadmin.entity.SeckillActivityModel;
import com.mntech.shopadmin.service.ISeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/seckill")
public class SeckillController {

    @Autowired
    private ISeckillService seckillService;

    // ==================== 管理端 ====================

    /**
     * 创建秒杀活动
     */
    @PostMapping("/admin/activity")
    public ApiResponse<SeckillActivityResponse> createActivity(@RequestBody SeckillActivityRequest request) {
        SeckillActivityResponse seckillActivityResponse = seckillService.createActivity(request);
        return ApiResponse.success("创建成功", seckillActivityResponse);
    }

    /**
     * 获取活动详情
     */
    @GetMapping("/admin/activity/{id}")
    public ApiResponse<SeckillActivityResponse> getActivityDetail(@PathVariable String id) {
        SeckillActivityResponse activity = seckillService.getActivityDetail(id);
        return ApiResponse.success("查询成功", activity);
    }

    /**
     * 更新秒杀活动
     */
    @PutMapping("/admin/activity/{id}")
    public ApiResponse<SeckillActivityResponse> updateActivity(@PathVariable String id, @RequestBody SeckillActivityRequest request) {
        SeckillActivityResponse seckillActivityResponse = seckillService.updateActivity(id, request);
        return ApiResponse.success("更新成功", seckillActivityResponse);
    }

    /**
     * 审核秒杀活动
     */
    @PutMapping("/admin/activity/{id}/audit")
    public ApiResponse<Void> auditActivity(@PathVariable String id, @RequestParam String status) {
        seckillService.auditActivity(id, status);
        return ApiResponse.success("审核成功", null);
    }

    /**
     * 删除秒杀活动
     */
    @DeleteMapping("/admin/activity/{id}")
    public ApiResponse<Void> deleteActivity(@PathVariable String id) {
        seckillService.deleteActivity(id);
        return ApiResponse.success("删除成功", null);
    }

    /**
     * 秒杀活动列表
     */
    @GetMapping("/admin/activity/list")
    public ApiResponse<Map<String, Object>> listActivities(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status) {
        IPage<SeckillActivityModel> seckillActivityModelIPage = seckillService.listActivities(page, size, name, status);
        Map<String, Object> response = new HashMap<>();
        response.put("list", seckillActivityModelIPage.getRecords());
        response.put("total", seckillActivityModelIPage.getTotal());
        response.put("page", seckillActivityModelIPage.getCurrent());
        response.put("pageSize", seckillActivityModelIPage.getSize());
        response.put("totalPages", seckillActivityModelIPage.getPages());
        return ApiResponse.success("查询成功", response);
    }

    /**
     * 获取活动商品列表（用于抽屉展示）
     */
    @GetMapping("/admin/product/list")
    public ApiResponse<List<SeckillProductResponse>> getActivityProducts(@RequestParam String activityId) {
        List<SeckillProductResponse> products = seckillService.getSeckillProducts(activityId);
        return ApiResponse.success("查询成功", products);
    }

    /**
     * 获取秒杀商品详情
     */
    @GetMapping("/admin/product/{id}")
    public ApiResponse<SeckillProductResponse> getProductDetail(@PathVariable String id) {
        SeckillProductResponse product = seckillService.getSeckillProductDetail(id);
        return ApiResponse.success("查询成功", product);
    }

    /**
     * 添加秒杀商品
     */
    @PostMapping("/admin/product")
    public ApiResponse<Void> addSeckillProduct(@RequestBody SeckillProductRequest request) {
        seckillService.addSeckillProduct(request);
        return ApiResponse.success("添加成功", null);
    }

    /**
     * 修改秒杀商品
     */
    @PutMapping("/admin/product/{id}")
    public ApiResponse<Void> updateSeckillProduct(@PathVariable String id, @RequestBody SeckillProductRequest request) {
        seckillService.updateSeckillProduct(id, request);
        return ApiResponse.success("修改成功", null);
    }

    /**
     * 删除秒杀商品
     */
    @DeleteMapping("/admin/product/{id}")
    public ApiResponse<Void> removeSeckillProduct(@PathVariable String id) {
        seckillService.removeSeckillProduct(id);
        return ApiResponse.success("删除成功", null);
    }


}