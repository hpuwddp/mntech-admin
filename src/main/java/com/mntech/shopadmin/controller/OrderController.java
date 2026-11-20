package com.mntech.shopadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mntech.shopadmin.dto.response.ApiResponse;
import com.mntech.shopadmin.entity.OrderModel;
import com.mntech.shopadmin.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;

    /**
     * 获取订单列表
     */
    @GetMapping
    public ApiResponse<Map<String, Object>> getOrders(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {

        IPage<OrderModel> orderPage = orderService.getOrders(orderNo, status, userId, page, limit);

        Map<String, Object> response = new HashMap<>();
        response.put("list", orderPage.getRecords());
        response.put("total", orderPage.getTotal());
        response.put("page", orderPage.getCurrent());
        response.put("pageSize", orderPage.getSize());
        response.put("totalPages", orderPage.getPages());

        return ApiResponse.success("获取订单列表成功", response);
    }

    /**
     * 获取订单详情（包含订单明细）
     */
    @GetMapping("/{id}")
    public ApiResponse<OrderModel> getOrderDetail(@PathVariable String id) {
        OrderModel order = orderService.getOrderDetail(id);
        return ApiResponse.success("获取订单详情成功", order);
    }

    /**
     * 获取订单明细
     */
    @GetMapping("/{id}/items")
    public ApiResponse<Object> getOrderItems(@PathVariable String id) {
        OrderModel order = orderService.getOrderDetail(id);
        return ApiResponse.success("获取订单明细成功", order.getItems());
    }

    /**
     * 更新订单状态
     */
    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateOrderStatus(
            @PathVariable String id,
            @RequestParam String status,
            @RequestParam(required = false) String remark) {
        orderService.updateOrderStatus(id, status, remark);
        return ApiResponse.success("更新订单状态成功");
    }
    /**
     * 更新订单状态
     * 和物流信息
     */
    @PutMapping("/{id}/wl")
    public ApiResponse<Void> updateOrderWl(
            @PathVariable String id,
            @RequestParam String wl,
            @RequestParam String wlCode) {
        orderService.updateOrderWl(id, wl,wlCode);
        return ApiResponse.success("更新订单状态成功");
    }
    /**
     * 取消订单
     */
    @PostMapping("/{id}/cancel")
    public ApiResponse<Void> cancelOrder(
            @PathVariable String id,
            @RequestParam(required = false) String reason) {
        orderService.cancelOrder(id, reason);
        return ApiResponse.success("订单取消成功");
    }

    /**
     * 删除订单
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteOrder(@PathVariable String id) {
        orderService.deleteOrder(id);
        return ApiResponse.success("订单删除成功");
    }
}
