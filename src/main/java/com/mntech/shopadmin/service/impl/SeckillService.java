package com.mntech.shopadmin.service.impl;

import cn.hutool.db.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mntech.shopadmin.dao.SeckillActivityMapper;
import com.mntech.shopadmin.dao.SeckillProductMapper;
import com.mntech.shopadmin.dto.SeckillActivityRequest;
import com.mntech.shopadmin.dto.SeckillProductRequest;
import com.mntech.shopadmin.dto.response.SeckillActivityResponse;
import com.mntech.shopadmin.dto.response.SeckillProductResponse;
import com.mntech.shopadmin.entity.SeckillActivityModel;
import com.mntech.shopadmin.entity.SeckillProductModel;
import com.mntech.shopadmin.exception.BusinessException;
import com.mntech.shopadmin.service.ISeckillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 秒杀服务实现（管理端）
 */
@Slf4j
@Service
public class SeckillService implements ISeckillService {

    @Autowired
    private SeckillActivityMapper activityMapper;

    @Autowired
    private SeckillProductMapper seckillProductMapper;

    // ==================== 活动管理 ====================

    @Override
    @Transactional
    public SeckillActivityResponse createActivity(SeckillActivityRequest request) {
        validateActivityRequest(request);

        SeckillActivityModel activity = new SeckillActivityModel();
        BeanUtils.copyProperties(request, activity);
        activity.setStatus("APPROVED");//默认审核通过
        activity.setCreatedBy("admin");

        activityMapper.insert(activity);

        // 保存商品
        if (request.getProducts() != null && !request.getProducts().isEmpty()) {
            for (SeckillProductRequest productReq : request.getProducts()) {
                SeckillProductModel product = new SeckillProductModel();
                BeanUtils.copyProperties(productReq, product);
                product.setActivityId(activity.getId());
                product.setRemainingStock(productReq.getStock());
                product.setSoldStock(0);
                product.setLockedStock(0);
                product.setSalesCount(0);
                product.setVersion(0);
                seckillProductMapper.insert(product);
            }
        }

        log.info("创建秒杀活动成功: {}", activity.getName());
        return getActivityDetail(activity.getId());
    }

    @Override
    @Transactional
    public SeckillActivityResponse updateActivity(String id, SeckillActivityRequest request) {
        SeckillActivityModel activity = activityMapper.selectById(id);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }

        if ("ONGOING".equals(activity.getStatus()) || "ENDED".equals(activity.getStatus())) {
            throw new BusinessException("已开始或已结束的活动不能修改");
        }

        BeanUtils.copyProperties(request, activity, "id", "status", "createdBy", "createdAt");
        activityMapper.updateById(activity);

        // 更新商品（先删后增）
        LambdaQueryWrapper<SeckillProductModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SeckillProductModel::getActivityId, id);
        seckillProductMapper.delete(wrapper);

        if (request.getProducts() != null && !request.getProducts().isEmpty()) {
            for (SeckillProductRequest productReq : request.getProducts()) {
                SeckillProductModel product = new SeckillProductModel();
                BeanUtils.copyProperties(productReq, product);
                product.setActivityId(id);
                product.setRemainingStock(productReq.getStock());
//                product.setSalesCount(0);
                seckillProductMapper.insert(product);
            }
        }

        log.info("更新秒杀活动成功: {}", activity.getName());
        return getActivityDetail(id);
    }

    @Override
    @Transactional
    public void deleteActivity(String id) {
        SeckillActivityModel activity = activityMapper.selectById(id);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }

        if ("ONGOING".equals(activity.getStatus())) {
            throw new BusinessException("进行中的活动不能删除");
        }

        LambdaQueryWrapper<SeckillProductModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SeckillProductModel::getActivityId, id);
        seckillProductMapper.delete(wrapper);
        activityMapper.deleteById(id);

        log.info("删除秒杀活动成功: {}", activity.getName());
    }

    @Override
    public SeckillActivityResponse getActivityDetail(String id) {
        SeckillActivityModel activity = activityMapper.selectById(id);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }
        return convertToActivityResponse(activity);
    }

    @Override
    public IPage<SeckillActivityModel> listActivities(Integer page, Integer size, String name, String status) {
        LambdaQueryWrapper<SeckillActivityModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SeckillActivityModel::getDeleted, 0);

        if (StringUtils.hasText(name)) {
            wrapper.like(SeckillActivityModel::getName, name);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(SeckillActivityModel::getStatus, status);
        }
        wrapper.orderByDesc(SeckillActivityModel::getCreatedAt);

        Page<SeckillActivityModel> pageParam = new Page<>(page, size);
        IPage<SeckillActivityModel> result = activityMapper.selectPage(pageParam, wrapper);

        // ✅ 统计每个活动的商品数
        for (SeckillActivityModel activity : result.getRecords()) {
            int count = seckillProductMapper.countByActivityId(activity.getId());
            activity.setProductCount(count);
        }

        return result;
    }

    @Override
    @Transactional
    public void auditActivity(String id, String status) {
        SeckillActivityModel activity = activityMapper.selectById(id);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }

        if (!"PENDING".equals(activity.getStatus())) {
            throw new BusinessException("只有待审核的活动才能审核");
        }

        if (!"APPROVED".equals(status) && !"REJECTED".equals(status)) {
            throw new BusinessException("审核状态不正确");
        }

        activity.setStatus(status);
        activityMapper.updateById(activity);
        log.info("审核秒杀活动: {}, 结果: {}", activity.getName(), status);
    }

    // ==================== 商品管理 ====================

    @Override
    @Transactional
    public void addSeckillProduct(SeckillProductRequest request) {
        SeckillActivityModel activity = activityMapper.selectById(request.getActivityId());
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }

        if ("ONGOING".equals(activity.getStatus()) || "ENDED".equals(activity.getStatus())) {
            throw new BusinessException("已开始或已结束的活动不能添加商品");
        }

        // 检查商品是否已存在
        LambdaQueryWrapper<SeckillProductModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SeckillProductModel::getActivityId, request.getActivityId())
                .eq(SeckillProductModel::getProductId, request.getProductId());
        if (request.getSkuId() != null) {
            wrapper.eq(SeckillProductModel::getSkuId, request.getSkuId());
        }
        Long count = seckillProductMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("该商品已添加到活动中");
        }

        SeckillProductModel product = new SeckillProductModel();
        BeanUtils.copyProperties(request, product);
        product.setRemainingStock(request.getStock());
//        product.setSalesCount(0);
        seckillProductMapper.insert(product);

        log.info("添加秒杀商品成功: activityId={}, productId={}", request.getActivityId(), request.getProductId());
    }

    @Override
    @Transactional
    public void updateSeckillProduct(String id, SeckillProductRequest request) {
        SeckillProductModel product = seckillProductMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("秒杀商品不存在");
        }

        SeckillActivityModel activity = activityMapper.selectById(product.getActivityId());
        if ("ONGOING".equals(activity.getStatus()) || "ENDED".equals(activity.getStatus())) {
            throw new BusinessException("已开始或已结束的活动不能修改商品");
        }

        product.setSeckillPrice(request.getSeckillPrice());
        product.setStock(request.getStock());
        product.setRemainingStock(request.getStock());
        product.setPerUserLimit(request.getPerUserLimit());
        product.setSortOrder(request.getSortOrder());
        seckillProductMapper.updateById(product);

        log.info("修改秒杀商品成功: {}", id);
    }

    @Override
    @Transactional
    public void removeSeckillProduct(String id) {
        SeckillProductModel product = seckillProductMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("秒杀商品不存在");
        }

        SeckillActivityModel activity = activityMapper.selectById(product.getActivityId());
        if ("ONGOING".equals(activity.getStatus()) || "ENDED".equals(activity.getStatus())) {
            throw new BusinessException("已开始或已结束的活动不能删除商品");
        }

        seckillProductMapper.deleteById(id);
        log.info("删除秒杀商品成功: {}", id);
    }

    @Override
    public List<SeckillProductResponse> getSeckillProducts(String activityId) {
//        LambdaQueryWrapper<SeckillProductModel> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(SeckillProductModel::getActivityId, activityId)
//                .eq(SeckillProductModel::getDeleted, 0)
//                .orderByAsc(SeckillProductModel::getSortOrder);

        List<SeckillProductModel> products = seckillProductMapper.selectByActivityId(activityId);
        return products.stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SeckillProductResponse getSeckillProductDetail(String id) {
        SeckillProductModel product = seckillProductMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("秒杀商品不存在");
        }
        return convertToProductResponse(product);
    }

    // ==================== 私有方法 ====================

    private void validateActivityRequest(SeckillActivityRequest request) {
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new BusinessException("开始时间不能晚于结束时间");
        }
        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("开始时间不能早于当前时间");
        }
        if (request.getProducts() == null || request.getProducts().isEmpty()) {
            throw new BusinessException("至少添加一个秒杀商品");
        }
        for (SeckillProductRequest product : request.getProducts()) {
            if (product.getStock() == null || product.getStock() <= 0) {
                throw new BusinessException("商品库存必须大于0");
            }
            if (product.getSeckillPrice() == null || product.getSeckillPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw new BusinessException("秒杀价必须大于0");
            }
        }
    }

    private SeckillActivityResponse convertToActivityResponse(SeckillActivityModel activity) {
        if (activity == null) {
            return null;
        }

        List<SeckillProductModel> products = seckillProductMapper.selectByActivityId(activity.getId());

        int totalStock = products.stream().mapToInt(SeckillProductModel::getStock).sum();
        int totalRemaining = products.stream().mapToInt(SeckillProductModel::getRemainingStock).sum();

        return SeckillActivityResponse.builder()
                .id(activity.getId())
                .name(activity.getName())
                .description(activity.getDescription())
                .startTime(activity.getStartTime())
                .endTime(activity.getEndTime())
                .status(activity.getStatus())
                .statusDesc(activity.getStatusDesc())
                .createdBy(activity.getCreatedBy())
                .createdAt(activity.getCreatedAt())
                .updatedAt(activity.getUpdatedAt())
                .productCount(products.size())
                .totalStock(totalStock)
                .totalRemainingStock(totalRemaining)
                .isOngoing("ONGOING".equals(activity.getStatus()))
                .isEnded("ENDED".equals(activity.getStatus()))
                .products(products.stream()
                        .map(this::convertToProductResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    private SeckillProductResponse convertToProductResponse(SeckillProductModel product) {
        return SeckillProductResponse.builder()
                .id(product.getId())
                .activityId(product.getActivityId())
                .productId(product.getProductId())
                .skuId(product.getSkuId())
                .seckillPrice(product.getSeckillPrice())
                .stock(product.getStock())
                .remainingStock(product.getRemainingStock())
                .perUserLimit(product.getPerUserLimit())
                .sortOrder(product.getSortOrder())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .isSoldOut(product.getRemainingStock() == null || product.getRemainingStock() <= 0)
                .productName(product.getProductName())
                .skuName(product.getSkuName())
                .build();
    }
}