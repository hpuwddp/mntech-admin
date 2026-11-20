package com.mntech.shopadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mntech.shopadmin.dao.GroupBuyActivityMapper;
import com.mntech.shopadmin.dao.GroupBuyOrderMapper;
import com.mntech.shopadmin.dao.GroupBuyProductMapper;
import com.mntech.shopadmin.dao.GroupBuyTeamMapper;
import com.mntech.shopadmin.dto.GroupBuyActivityRequest;
import com.mntech.shopadmin.dto.GroupBuyProductRequest;
import com.mntech.shopadmin.dto.response.GroupBuyActivityResponse;
import com.mntech.shopadmin.dto.response.GroupBuyOrderResponse;
import com.mntech.shopadmin.dto.response.GroupBuyProductResponse;
import com.mntech.shopadmin.dto.response.GroupBuyTeamResponse;
import com.mntech.shopadmin.entity.GroupBuyActivityModel;
import com.mntech.shopadmin.entity.GroupBuyOrderModel;
import com.mntech.shopadmin.entity.GroupBuyProductModel;
import com.mntech.shopadmin.entity.GroupBuyTeamModel;
import com.mntech.shopadmin.exception.BusinessException;
import com.mntech.shopadmin.service.IGroupBuyAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 团购管理服务（管理端）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GroupBuyAdminService implements IGroupBuyAdminService {

    private final GroupBuyActivityMapper activityMapper;
    private final GroupBuyProductMapper productMapper;
    private final GroupBuyTeamMapper teamMapper;
    private final GroupBuyOrderMapper orderMapper;

    // ==================== 活动管理 ====================

    @Override
    @Transactional
    public GroupBuyActivityResponse createActivity(GroupBuyActivityRequest request) {
        validateActivityRequest(request);

        GroupBuyActivityModel activity = new GroupBuyActivityModel();
        BeanUtils.copyProperties(request, activity);
        activity.setStatus("PENDING"); // 新建待审核
        activity.setCreatedBy("admin");

        activityMapper.insert(activity);

        // 保存商品
        if (request.getProducts() != null && !request.getProducts().isEmpty()) {
            for (GroupBuyProductRequest productReq : request.getProducts()) {
                productReq.setActivityId(activity.getId());
                insertProduct(productReq);
            }
        }

        log.info("创建团购活动成功: {}", activity.getName());
        return getActivityDetail(activity.getId());
    }

    @Override
    @Transactional
    public GroupBuyActivityResponse updateActivity(String id, GroupBuyActivityRequest request) {
        GroupBuyActivityModel activity = activityMapper.selectById(id);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }
        if ("ONGOING".equals(activity.getStatus()) || "ENDED".equals(activity.getStatus())) {
            throw new BusinessException("已开始或已结束的活动不能修改");
        }

        BeanUtils.copyProperties(request, activity, "id", "status", "createdBy", "createdAt");
        activityMapper.updateById(activity);

        // 更新商品（先删后增，仅限非进行中的活动）
        LambdaQueryWrapper<GroupBuyProductModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupBuyProductModel::getActivityId, id);
        productMapper.delete(wrapper);

        if (request.getProducts() != null && !request.getProducts().isEmpty()) {
            for (GroupBuyProductRequest productReq : request.getProducts()) {
                productReq.setActivityId(id);
                insertProduct(productReq);
            }
        }

        log.info("更新团购活动成功: {}", activity.getName());
        return getActivityDetail(id);
    }

    @Override
    @Transactional
    public void deleteActivity(String id) {
        GroupBuyActivityModel activity = activityMapper.selectById(id);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }
        if ("ONGOING".equals(activity.getStatus())) {
            throw new BusinessException("进行中的活动不能删除");
        }

        LambdaQueryWrapper<GroupBuyProductModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupBuyProductModel::getActivityId, id);
        productMapper.delete(wrapper);
        activityMapper.deleteById(id);

        log.info("删除团购活动成功: {}", activity.getName());
    }

    @Override
    public GroupBuyActivityResponse getActivityDetail(String id) {
        GroupBuyActivityModel activity = activityMapper.selectById(id);
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }

        List<GroupBuyProductModel> products = productMapper.selectByActivityIdWithProduct(id);
        int totalStock = products.stream().mapToInt(p -> p.getStock() == null ? 0 : p.getStock()).sum();
        int totalRemaining = products.stream().mapToInt(p -> p.getRemainingStock() == null ? 0 : p.getRemainingStock()).sum();

        return GroupBuyActivityResponse.builder()
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
                .products(products.stream().map(this::convertToProductResponse).collect(Collectors.toList()))
                .isOngoing("ONGOING".equals(activity.getStatus()))
                .isEnded("ENDED".equals(activity.getStatus()))
                .isPending("PENDING".equals(activity.getStatus()))
                .build();
    }

    @Override
    public IPage<GroupBuyActivityModel> listActivities(Integer page, Integer size, String name, String status) {
        LambdaQueryWrapper<GroupBuyActivityModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupBuyActivityModel::getDeleted, 0);

        if (StringUtils.hasText(name)) {
            wrapper.like(GroupBuyActivityModel::getName, name);
        }
        if (StringUtils.hasText(status) && !"ALL".equals(status)) {
            wrapper.eq(GroupBuyActivityModel::getStatus, status);
        }
        wrapper.orderByDesc(GroupBuyActivityModel::getCreatedAt);

        Page<GroupBuyActivityModel> pageParam = new Page<>(page, size);
        IPage<GroupBuyActivityModel> result = activityMapper.selectPage(pageParam, wrapper);

        for (GroupBuyActivityModel activity : result.getRecords()) {
            activity.setProductCount(activityMapper.countProducts(activity.getId()));
        }
        return result;
    }

    @Override
    @Transactional
    public void auditActivity(String id, String status) {
        GroupBuyActivityModel activity = activityMapper.selectById(id);
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
        log.info("审核团购活动: {}, 结果: {}", activity.getName(), status);
    }

    // ==================== 商品管理 ====================

    @Override
    @Transactional
    public void addProduct(GroupBuyProductRequest request) {
        GroupBuyActivityModel activity = activityMapper.selectById(request.getActivityId());
        if (activity == null) {
            throw new BusinessException("活动不存在");
        }
        if ("ONGOING".equals(activity.getStatus()) || "ENDED".equals(activity.getStatus())) {
            throw new BusinessException("已开始或已结束的活动不能添加商品");
        }

        // 检查商品是否已存在
        LambdaQueryWrapper<GroupBuyProductModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupBuyProductModel::getActivityId, request.getActivityId())
                .eq(GroupBuyProductModel::getProductId, request.getProductId());
        Long count = productMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            throw new BusinessException("该商品已添加到活动中");
        }

        insertProduct(request);
        log.info("添加团购商品成功: activityId={}, productId={}", request.getActivityId(), request.getProductId());
    }

    @Override
    @Transactional
    public void updateProduct(String id, GroupBuyProductRequest request) {
        GroupBuyProductModel product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("团购商品不存在");
        }

        GroupBuyActivityModel activity = activityMapper.selectById(product.getActivityId());
        if (activity != null && ("ONGOING".equals(activity.getStatus()) || "ENDED".equals(activity.getStatus()))) {
            throw new BusinessException("已开始或已结束的活动不能修改商品");
        }

        product.setGroupPrice(request.getGroupPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setStock(request.getStock());
        product.setRemainingStock(request.getStock());
        product.setPerUserLimit(request.getPerUserLimit());
        product.setMinPeople(request.getMinPeople());
        product.setGroupHours(request.getGroupHours());
        product.setSortOrder(request.getSortOrder());
        productMapper.updateById(product);

        log.info("修改团购商品成功: {}", id);
    }

    @Override
    @Transactional
    public void removeProduct(String id) {
        GroupBuyProductModel product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("团购商品不存在");
        }

        GroupBuyActivityModel activity = activityMapper.selectById(product.getActivityId());
        if (activity != null && ("ONGOING".equals(activity.getStatus()) || "ENDED".equals(activity.getStatus()))) {
            throw new BusinessException("已开始或已结束的活动不能删除商品");
        }

        productMapper.deleteById(id);
        log.info("删除团购商品成功: {}", id);
    }

    @Override
    public List<GroupBuyProductResponse> getActivityProducts(String activityId) {
        List<GroupBuyProductModel> products = productMapper.selectByActivityIdWithProduct(activityId);
        return products.stream().map(this::convertToProductResponse).collect(Collectors.toList());
    }

    @Override
    public GroupBuyProductResponse getProductDetail(String id) {
        GroupBuyProductModel product = productMapper.selectByIdWithProduct(id);
        if (product == null) {
            throw new BusinessException("团购商品不存在");
        }
        return convertToProductResponse(product);
    }

    // ==================== 团记录 ====================

    @Override
    public IPage<GroupBuyTeamModel> listTeams(Integer page, Integer size, String groupNo, String activityId, String status) {
        Page<GroupBuyTeamModel> pageParam = new Page<>(page, size);
        return teamMapper.selectPageWithJoin(pageParam, groupNo, activityId, status);
    }

    // ==================== 订单 ====================

    @Override
    public IPage<GroupBuyOrderModel> listOrders(Integer page, Integer size, String orderNo, String userId, String status) {
        Page<GroupBuyOrderModel> pageParam = new Page<>(page, size);
        return orderMapper.selectPageWithJoin(pageParam, orderNo, userId, status);
    }

    @Override
    public GroupBuyOrderResponse getOrderDetail(String orderId) {
        GroupBuyOrderModel order = orderMapper.selectByIdWithJoin(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return convertToOrderResponse(order);
    }

    // ==================== 私有方法 ====================

    private void insertProduct(GroupBuyProductRequest request) {
        GroupBuyProductModel product = new GroupBuyProductModel();
        BeanUtils.copyProperties(request, product);
        product.setRemainingStock(request.getStock());
        product.setLockedStock(0);
        product.setSoldStock(0);
        product.setSalesCount(0);
        product.setVersion(0);
        productMapper.insert(product);
    }

    private void validateActivityRequest(GroupBuyActivityRequest request) {
        if (request.getStartTime() != null && request.getEndTime() != null
                && request.getStartTime().isAfter(request.getEndTime())) {
            throw new BusinessException("开始时间不能晚于结束时间");
        }
        if (request.getProducts() == null || request.getProducts().isEmpty()) {
            throw new BusinessException("至少添加一个团购商品");
        }
        for (GroupBuyProductRequest product : request.getProducts()) {
            if (product.getStock() == null || product.getStock() <= 0) {
                throw new BusinessException("商品库存必须大于0");
            }
            if (product.getGroupPrice() == null || product.getGroupPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw new BusinessException("团购价必须大于0");
            }
        }
    }

    private GroupBuyProductResponse convertToProductResponse(GroupBuyProductModel product) {
        if (product == null) {
            return null;
        }
        return GroupBuyProductResponse.builder()
                .id(product.getId())
                .activityId(product.getActivityId())
                .productId(product.getProductId())
                .skuId(product.getSkuId())
                .groupPrice(product.getGroupPrice())
                .originalPrice(product.getOriginalPrice())
                .stock(product.getStock())
                .remainingStock(product.getRemainingStock())
                .perUserLimit(product.getPerUserLimit())
                .minPeople(product.getMinPeople())
                .groupHours(product.getGroupHours())
                .sortOrder(product.getSortOrder())
                .salesCount(product.getSalesCount())
                .lockedStock(product.getLockedStock())
                .soldStock(product.getSoldStock())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .productName(product.getProductName())
                .imageUrl(product.getImageUrl())
                .isSoldOut(product.getIsSoldOut())
                .groupPriceDisplay(product.getGroupPriceDisplay())
                .discountDisplay(product.getDiscountDisplay())
                .build();
    }

    private GroupBuyTeamResponse convertToTeamResponse(GroupBuyTeamModel team) {
        if (team == null) {
            return null;
        }
        return GroupBuyTeamResponse.builder()
                .id(team.getId())
                .groupNo(team.getGroupNo())
                .activityId(team.getActivityId())
                .activityName(team.getActivityName())
                .groupBuyProductId(team.getGroupBuyProductId())
                .leaderUserId(team.getLeaderUserId())
                .leaderNickname(team.getLeaderNickname())
                .leaderAvatar(team.getLeaderAvatar())
                .currentNum(team.getCurrentNum())
                .targetNum(team.getTargetNum())
                .status(team.getStatus())
                .statusDesc(team.getStatusDesc())
                .endTime(team.getEndTime())
                .createdAt(team.getCreatedAt())
                .productName(team.getProductName())
                .productImage(team.getProductImage())
                .build();
    }

    private GroupBuyOrderResponse convertToOrderResponse(GroupBuyOrderModel order) {
        if (order == null) {
            return null;
        }
        return GroupBuyOrderResponse.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .teamId(order.getTeamId())
                .groupNo(order.getGroupNo())
                .activityId(order.getActivityId())
                .activityName(order.getActivityName())
                .groupBuyProductId(order.getGroupBuyProductId())
                .userId(order.getUserId())
                .buyerNickname(order.getBuyerNickname())
                .buyerAvatar(order.getBuyerAvatar())
                .quantity(order.getQuantity())
                .groupPrice(order.getGroupPrice())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .statusDesc(order.getStatusDesc())
                .payTime(order.getPayTime())
                .transactionId(order.getTransactionId())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .receiverAddress(order.getReceiverAddress())
                .createdAt(order.getCreatedAt())
                .productName(order.getProductName())
                .productImage(order.getProductImage())
                .teamStatus(order.getTeamStatus())
                .build();
    }
}