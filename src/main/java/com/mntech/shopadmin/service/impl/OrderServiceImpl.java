package com.mntech.shopadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mntech.shopadmin.dao.OrderItemMapper;
import com.mntech.shopadmin.dao.OrderMapper;
import com.mntech.shopadmin.entity.OrderItemModel;
import com.mntech.shopadmin.entity.OrderModel;
import com.mntech.shopadmin.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    public IPage<OrderModel> getOrders(String orderNo, String status, String userId, Integer page, Integer limit) {
        Page<OrderModel> pageParam = new Page<>(page, limit);

        LambdaQueryWrapper<OrderModel> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(orderNo)) {
            wrapper.like(OrderModel::getOrderNo, orderNo);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(OrderModel::getStatus, status);
        }
        if (StringUtils.hasText(userId)) {
            wrapper.eq(OrderModel::getUserId, userId);
        }

        wrapper.orderByDesc(OrderModel::getCreatedAt);
        IPage<OrderModel> orderModelIPage= orderMapper.selectPage(pageParam,wrapper);
        return orderModelIPage;
    }

    @Override
    public OrderModel getOrderDetail(String id) {
        // 查询订单
        OrderModel order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 查询订单明细
        LambdaQueryWrapper<OrderItemModel> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(OrderItemModel::getOrderNo, order.getOrderNo());
        itemWrapper.orderByAsc(OrderItemModel::getCreatedAt);

        List<OrderItemModel> items = orderItemMapper.selectList(itemWrapper);
        order.setItems(items);

        return order;
    }

    @Override
    @Transactional
    public void updateOrderStatus(String id, String status, String remark) {
        OrderModel order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());

        // 如果状态是已支付，记录支付时间
        if ("PAID".equals(status)) {
            order.setPayTime(LocalDateTime.now());
        }

        orderMapper.updateById(order);
    }

    @Override
    public void updateOrderWl(String id, String wl, String wlCode) {
        OrderModel order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        order.setWl(wl);
        order.setWlCode(wlCode);
        order.setStatus("SEND");
        order.setUpdatedAt(LocalDateTime.now());

        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void cancelOrder(String id, String reason) {
        OrderModel order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 只有待支付和已支付的订单可以取消
        if (!"PENDING".equals(order.getStatus()) && !"PAID".equals(order.getStatus())) {
            throw new RuntimeException("当前状态不允许取消订单");
        }

        order.setStatus("CANCELLED");
        order.setRemark(reason != null ? reason : "用户取消订单");
        order.setUpdatedAt(LocalDateTime.now());

        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void deleteOrder(String id) {
        OrderModel order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        orderMapper.deleteById(id);
        // 逻辑删除订单明细
        LambdaQueryWrapper<OrderItemModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItemModel::getOrderNo, order.getOrderNo());
        orderItemMapper.delete(wrapper);
    }
}
