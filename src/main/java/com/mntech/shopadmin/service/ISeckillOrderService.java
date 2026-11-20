package com.mntech.shopadmin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mntech.shopadmin.dto.response.SeckillOrderResponse;
import com.mntech.shopadmin.entity.SeckillOrderModel;

public interface ISeckillOrderService {
    /**
     * 分页查询秒杀订单列表
     */
    IPage<SeckillOrderModel> listOrders(Integer page, Integer size, String orderNo, String userId, String status);

    /**
     * 获取秒杀订单详情
     */
    SeckillOrderResponse getOrderDetail(String orderId);

    /**
     * 取消秒杀订单
     */
    void cancelOrder(String orderId, String reason);

    /**
     * 退款
     */
    void refundOrder(String orderId);

    /**
     * 删除秒杀订单
     */
    void deleteOrder(String orderId);
}
