package com.mntech.shopadmin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mntech.shopadmin.entity.OrderModel;

public interface IOrderService {
    IPage<OrderModel> getOrders(String orderNo, String status, String userId, Integer page, Integer limit);
    OrderModel getOrderDetail(String id);
    void updateOrderStatus(String id, String status, String remark);
    void updateOrderWl(String id, String wl, String wlCode);
    void cancelOrder(String id, String reason);
    void deleteOrder(String id);
}
