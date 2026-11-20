package com.mntech.shopadmin.service;

import cn.hutool.db.PageResult;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mntech.shopadmin.dto.SeckillActivityRequest;
import com.mntech.shopadmin.dto.SeckillProductRequest;
import com.mntech.shopadmin.dto.response.SeckillActivityResponse;
import com.mntech.shopadmin.dto.response.SeckillProductResponse;
import com.mntech.shopadmin.entity.SeckillActivityModel;

import java.util.List;

public interface ISeckillService {
    /**
     * 创建秒杀活动
     */
    SeckillActivityResponse createActivity(SeckillActivityRequest request);

    /**
     * 更新秒杀活动
     */
    SeckillActivityResponse updateActivity(String id, SeckillActivityRequest request);

    /**
     * 删除秒杀活动
     */
    void deleteActivity(String id);

    /**
     * 获取活动详情
     */
    SeckillActivityResponse getActivityDetail(String id);

    /**
     * 分页查询活动列表
     */
    IPage<SeckillActivityModel> listActivities(Integer page, Integer size, String name, String status);

    /**
     * 审核秒杀活动
     */
    void auditActivity(String id, String status);

    // ==================== 商品管理 ====================

    /**
     * 添加秒杀商品
     */
    void addSeckillProduct(SeckillProductRequest request);

    /**
     * 修改秒杀商品
     */
    void updateSeckillProduct(String id, SeckillProductRequest request);

    /**
     * 删除秒杀商品
     */
    void removeSeckillProduct(String id);

    /**
     * 获取活动商品列表
     */
    List<SeckillProductResponse> getSeckillProducts(String activityId);

    /**
     * 获取秒杀商品详情
     */
    SeckillProductResponse getSeckillProductDetail(String id);


}
