package com.nikki.finalproject.controller;

import com.nikki.finalproject.biz.OrderBiz;
import com.nikki.finalproject.entity.Order;
import com.nikki.finalproject.entity.OrderItem;
import com.nikki.finalproject.common.Result;
import com.nikki.finalproject.mapper.OrderItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderBiz orderBiz;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @GetMapping("/list")
    public Result<Map<String, Object>> listOrders(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status) {
        return orderBiz.getOrderList(pageNum, pageSize, orderNo, username, status);
    }

    @GetMapping("/detail/{orderId}")
    public Result<Map<String, Object>> getOrderDetail(@PathVariable String orderId) {
        return orderBiz.getOrderDetail(orderId);
    }

    @PostMapping("/add")
    public Result<String> addOrder(@RequestBody Order order) {
        return orderBiz.addOrder(order);
    }

    @PostMapping("/update")
    public Result<String> updateOrder(@RequestBody Order order) {
        return orderBiz.updateOrder(order);
    }

    @PostMapping("/cancel/{orderId}")
    public Result<String> cancelOrder(@PathVariable String orderId) {
        return orderBiz.cancelOrder(orderId);
    }

    @GetMapping("/items/{orderId}")
    public Result<List<OrderItem>> getOrderItems(@PathVariable String orderId) {
        List<OrderItem> items = orderItemMapper.selectItemsByOrderId(orderId);
        return Result.success(items);
    }

    @PostMapping("/ship")
    public Result<String> shipOrder(@RequestParam String orderId,
                            @RequestParam String deliveryCompany,
                            @RequestParam String deliveryNumber) {
        return orderBiz.shipOrder(orderId, deliveryCompany, deliveryNumber);
    }
    
    @DeleteMapping("/delete/{orderId}")
    public Result<String> deleteOrder(@PathVariable String orderId) {
        return orderBiz.deleteOrder(orderId);
    }
} 