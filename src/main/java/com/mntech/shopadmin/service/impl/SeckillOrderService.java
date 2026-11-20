package com.mntech.shopadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mntech.shopadmin.dao.ProductMapper;
import com.mntech.shopadmin.dao.SeckillOrderMapper;
import com.mntech.shopadmin.dao.SeckillProductMapper;
import com.mntech.shopadmin.dto.response.SeckillOrderResponse;
import com.mntech.shopadmin.entity.ProductModel;
import com.mntech.shopadmin.entity.SeckillOrderModel;
import com.mntech.shopadmin.entity.SeckillProductModel;
import com.mntech.shopadmin.exception.BusinessException;
import com.mntech.shopadmin.service.ISeckillOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeckillOrderService implements ISeckillOrderService {

    private final SeckillOrderMapper seckillOrderMapper;
    private final SeckillProductMapper seckillProductMapper;
    private final ProductMapper productMapper;

    @Override
    public IPage<SeckillOrderModel> listOrders(Integer page, Integer size, String orderNo, String userId, String status) {
        LambdaQueryWrapper<SeckillOrderModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SeckillOrderModel::getDeleted, 0);

        if (StringUtils.hasText(orderNo)) {
            wrapper.like(SeckillOrderModel::getOrderNo, orderNo);
        }
        if (StringUtils.hasText(userId)) {
            wrapper.eq(SeckillOrderModel::getUserId, userId);
        }
        if (StringUtils.hasText(status) && !"ALL".equals(status)) {
            wrapper.eq(SeckillOrderModel::getStatus, status);
        }
        wrapper.orderByDesc(SeckillOrderModel::getCreatedAt);

        Page<SeckillOrderModel> pageParam = new Page<>(page, size);
        return seckillOrderMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public SeckillOrderResponse getOrderDetail(String orderId) {
        SeckillOrderModel order = seckillOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        SeckillOrderResponse response = new SeckillOrderResponse();
        BeanUtils.copyProperties(order, response);
        response.setOrderId(order.getId());
        response.setTotalAmount(order.getSeckillPrice().multiply(BigDecimal.valueOf(order.getQuantity())));

        // 查询商品信息
        SeckillProductModel seckillProduct = seckillProductMapper.selectById(order.getSeckillProductId());
        if (seckillProduct != null) {
            response.setSeckillProductId(seckillProduct.getId());
            // 查询商品名称
            ProductModel product = productMapper.selectById(seckillProduct.getProductId());
            if (product != null) {
                response.setProductName(product.getName());
                response.setProductImage(product.getMainImage());
            }
        }

        // 查询活动名称
        // 如果有活动表，可以关联查询
        // response.setActivityName(activity.getName());

        return response;
    }

    @Override
    @Transactional
    public void cancelOrder(String orderId, String reason) {
        SeckillOrderModel order = seckillOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (!"PENDING".equals(order.getStatus())) {
            throw new BusinessException("当前订单状态不能取消");
        }

        // 更新订单状态
        order.setStatus("CANCELLED");
        order.setUpdatedAt(LocalDateTime.now());
        seckillOrderMapper.updateById(order);

        // 回退库存
        seckillProductMapper.increaseStock(order.getSeckillProductId(), order.getQuantity());

        log.info("取消秒杀订单: orderId={}, reason={}", orderId, reason);
    }

    @Override
    @Transactional
    public void refundOrder(String orderId) {
        SeckillOrderModel order = seckillOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (!"PAID".equals(order.getStatus())) {
            throw new BusinessException("只有已支付的订单才能退款");
        }

        // 更新订单状态为退款
        order.setStatus("REFUND");
        order.setUpdatedAt(LocalDateTime.now());
        seckillOrderMapper.updateById(order);

        // 回退库存
        seckillProductMapper.increaseStock(order.getSeckillProductId(), order.getQuantity());

        log.info("秒杀订单退款: orderId={}", orderId);
    }

    @Override
    @Transactional
    public void deleteOrder(String orderId) {
        SeckillOrderModel order = seckillOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 逻辑删除
        order.setDeleted(1);
        seckillOrderMapper.updateById(order);

        log.info("删除秒杀订单: orderId={}", orderId);
    }
}