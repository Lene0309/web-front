package com.nikki.finalproject.biz;

import com.nikki.finalproject.entity.Order;
import com.nikki.finalproject.entity.OrderItem;
import com.nikki.finalproject.entity.Delivery;
import com.nikki.finalproject.mapper.DeliveryMapper;
import com.nikki.finalproject.mapper.OrderItemMapper;
import com.nikki.finalproject.mapper.OrderMapper;
import com.nikki.finalproject.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OrderBiz {
    private static final Logger log = LoggerFactory.getLogger(OrderBiz.class);

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private DeliveryMapper deliveryMapper;

    @Cacheable(value = "orderList", key = "#pageNum + '-' + #pageSize + '-' + #orderNo + '-' + #username + '-' + #status")
    public Result<Map<String, Object>> getOrderList(int pageNum, int pageSize, String orderNo, String username, Integer status) {
        try {
            int offset = (pageNum - 1) * pageSize;
            List<Order> orders = orderMapper.selectOrderList(orderNo, username, status, offset, pageSize);
            int total = orderMapper.countOrderList(orderNo, username, status);

            for (Order order : orders) {
                if (order.getOrderId() != null) {
                    order.setItems(orderItemMapper.selectItemsByOrderId(order.getOrderId()));
                    order.setDeliveryInfo(deliveryMapper.selectByOrderId(order.getOrderId()));
                }
            }

            Map<String, Object> data = new HashMap<>();
            data.put("list", orders);
            data.put("total", total);
            
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取订单列表失败", e);
            return Result.error("获取订单列表失败: " + e.getMessage());
        }
    }

    public Result<Map<String, Object>> getOrderDetail(String orderId) {
        try {
            Order order = orderMapper.selectOrderById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }
            List<OrderItem> items = orderItemMapper.selectItemsByOrderId(orderId);
            order.setItems(items);
            
            // 查询配送信息
            order.setDeliveryInfo(deliveryMapper.selectByOrderId(orderId));

            String productName = "";
            String categoryName = "";
            if (items != null && !items.isEmpty()) {
                productName = items.get(0).getProductName();
                categoryName = items.get(0).getCategoryName();
            }

            Map<String, Object> data = new HashMap<>();
            data.put("orderId", order.getOrderId());
            data.put("username", order.getUsername());
            data.put("productName", productName);
            data.put("categoryName", categoryName);
            data.put("status", order.getStatus());
            data.put("totalAmount", order.getTotalAmount());
            data.put("receiverName", order.getReceiverName());
            data.put("receiverPhone", order.getReceiverPhone());
            data.put("shippingAddress", order.getShippingAddress());
            data.put("customerId", order.getCustomerId());
            data.put("createTime", order.getCreateTime());
            data.put("paymentTime", order.getPaymentTime());
            data.put("shippingTime", order.getShippingTime());
            data.put("completeTime", order.getCompleteTime());
            data.put("items", items); // 添加商品信息
            data.put("deliveryInfo", order.getDeliveryInfo()); // 添加配送信息
            return Result.success(data);

        } catch (Exception e) {
            log.error("获取订单详情失败", e);
            return Result.error("系统错误");
        }
    }

    @CacheEvict(value = {"orderList"}, allEntries = true)
    @Transactional
    public Result<String> addOrder(Order order) {
        try {
            // 自动生成 orderId，防止为 null
            if (order.getOrderId() == null || order.getOrderId().isEmpty()) {
                order.setOrderId(java.util.UUID.randomUUID().toString().replaceAll("-", ""));
            }
            // 检查订单号是否已存在
            Order existingOrder = orderMapper.selectOrderById(order.getOrderId());
            if (existingOrder != null) {
                return Result.error("订单号已存在");
            }

            // 计算订单总金额
            List<OrderItem> items = order.getItems();
            if (items == null || items.isEmpty()) {
                return Result.error("订单商品列表不能为空");
            }

            // 保证status有值
            if (order.getStatus() == null) {
                order.setStatus(0); // 0表示新订单/待支付
            }

            BigDecimal totalAmount = items.stream()
                    .map(item -> item.getCurrentPrice().multiply(new BigDecimal(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            order.setTotalAmount(totalAmount);

            // 插入订单主表
            int orderResult = orderMapper.insertOrder(order);
            if (orderResult <= 0) {
                return Result.error("创建订单失败");
            }

            // 插入订单商品明细，自动生成 item_id
            for (OrderItem item : items) {
                if (item.getItemId() == null || item.getItemId().isEmpty()) {
                    item.setItemId(java.util.UUID.randomUUID().toString().replaceAll("-", ""));
                }
                item.setOrderId(order.getOrderId());
                // 关键：给totalPrice赋值，防止为null
                item.setTotalPrice(item.getCurrentPrice().multiply(new BigDecimal(item.getQuantity())));
                int itemResult = orderItemMapper.insert(item);
                if (itemResult <= 0) {
                    throw new RuntimeException("插入订单商品失败");
                }
            }

            return Result.success("订单创建成功", "订单创建成功");
        } catch (Exception e) {
            log.error("添加订单失败", e);
            return Result.error("添加订单失败: " + e.getMessage());
        }
    }

    @CacheEvict(value = {"orderList"}, allEntries = true)
    @Transactional
    public Result<String> updateOrder(Order order) {
        try {
            Order existingOrder = orderMapper.selectOrderById(order.getOrderId());
            if (existingOrder == null) {
                return Result.error("订单不存在");
            }
            if (order.getCustomerId() == null) {
                return Result.error("客户ID不能为空");
            }
            List<OrderItem> items = order.getItems();
            if (items == null || items.isEmpty()) {
                return Result.error("订单商品列表不能为空");
            }
            BigDecimal totalAmount = items.stream()
                    .map(item -> item.getCurrentPrice().multiply(new BigDecimal(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            order.setTotalAmount(totalAmount);

            int orderResult = orderMapper.updateOrder(order);
            if (orderResult <= 0) {
                return Result.error("更新订单失败");
            }

            orderItemMapper.deleteByOrderId(order.getOrderId());
            for (OrderItem item : items) {
                if (item.getItemId() == null || item.getItemId().isEmpty()) {
                    item.setItemId(java.util.UUID.randomUUID().toString().replaceAll("-", ""));
                }
                item.setOrderId(order.getOrderId());
                // 关键：给totalPrice赋值
                item.setTotalPrice(item.getCurrentPrice().multiply(new BigDecimal(item.getQuantity())));
                int itemResult = orderItemMapper.insert(item);
                if (itemResult <= 0) {
                    throw new RuntimeException("插入订单商品失败");
                }
            }

            return Result.success("订单更新成功", "订单更新成功");
        } catch (Exception e) {
            log.error("更新订单失败", e);
            return Result.error("更新订单失败: " + e.getMessage());
        }
    }

    @CacheEvict(value = {"orderList"}, allEntries = true)
    public Result<String> cancelOrder(String orderId) {
        try {
            int affectedRows = orderMapper.updateOrderStatus(orderId, 4); // 4表示已取消
            return affectedRows > 0 ?
                    Result.success("订单取消成功", "订单取消成功") :
                    Result.error("订单取消失败，可能订单状态不符合要求");
        } catch (Exception e) {
            return Result.error("取消订单失败: " + e.getMessage());
        }
    }

    public Result<String> shipOrder(String orderId, String deliveryCompany, String deliveryNumber) {
        try {
            int affectedRows = orderMapper.updateOrderStatus(orderId, 2);
            if (affectedRows == 0) {
                return Result.error("发货失败，订单状态不符合要求");
            }
            Delivery delivery = new Delivery();
            delivery.setOrderId(orderId);
            delivery.setDeliveryCompany(deliveryCompany);
            delivery.setDeliveryNumber(deliveryNumber);
            delivery.setDeliveryStatus(1); // 1表示已发货

            affectedRows = deliveryMapper.insert(delivery);
            return affectedRows > 0 ?
                    Result.success("发货成功", "发货成功") :
                    Result.error("发货信息保存失败");
        } catch (Exception e) {
            return Result.error("发货失败: " + e.getMessage());
        }
    }

    @CacheEvict(value = {"orderList"}, allEntries = true)
    @Transactional
    public Result<String> deleteOrder(String orderId) {
        try {
            orderItemMapper.deleteByOrderId(orderId);
            int affectedRows = orderMapper.deleteOrder(orderId);
            return affectedRows > 0 ? Result.success("删除成功", "删除成功") : Result.error("删除失败");
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }
} 