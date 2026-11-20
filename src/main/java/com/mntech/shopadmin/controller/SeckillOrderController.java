package com.mntech.shopadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mntech.shopadmin.dto.response.ApiResponse;
import com.mntech.shopadmin.dto.response.SeckillOrderResponse;
import com.mntech.shopadmin.entity.SeckillOrderModel;
import com.mntech.shopadmin.service.ISeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/seckill/admin")
public class SeckillOrderController {

    @Autowired
    private ISeckillOrderService seckillOrderService;

    /**
     * 分页查询秒杀订单列表
     */
    @GetMapping("/orders")
    public  ApiResponse<Map<String, Object>> listOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String status) {

        IPage<SeckillOrderModel> result = seckillOrderService.listOrders(page, size, orderNo, userId, status);

        Map<String, Object> response = new HashMap<>();
        response.put("list", result.getRecords());
        response.put("total", result.getTotal());
        response.put("page", result.getCurrent());
        response.put("pageSize", result.getSize());
        response.put("totalPages", result.getPages());

        return ApiResponse.success("查询成功", response);
    }

    /**
     * 获取秒杀订单详情
     */
    @GetMapping("/order/{orderId}")
    public ApiResponse<SeckillOrderResponse> getOrderDetail(@PathVariable String orderId) {
        SeckillOrderResponse order = seckillOrderService.getOrderDetail(orderId);
        return ApiResponse.success("查询成功", order);
    }

    /**
     * 取消秒杀订单
     */
    @PutMapping("/order/{orderId}/cancel")
    public ApiResponse<Void> cancelOrder(@PathVariable String orderId, @RequestParam(required = false) String reason) {
        seckillOrderService.cancelOrder(orderId, reason);
        return ApiResponse.success("取消成功", null);
    }

    /**
     * 退款
     */
    @PutMapping("/order/{orderId}/refund")
    public ApiResponse<Void> refundOrder(@PathVariable String orderId) {
        seckillOrderService.refundOrder(orderId);
        return ApiResponse.success("退款成功", null);
    }

    /**
     * 删除秒杀订单
     */
    @DeleteMapping("/order/{orderId}")
    public ApiResponse<Void> deleteOrder(@PathVariable String orderId) {
        seckillOrderService.deleteOrder(orderId);
        return ApiResponse.success("删除成功", null);
    }
}