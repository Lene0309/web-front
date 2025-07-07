package com.nikki.finalproject.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String orderId;
    private String customerId;
    private String username;
    private BigDecimal totalAmount;
    private Integer status; // 0:待付款 1:待发货 2:已发货 3:已完成 4:已取消
    private String receiverName;
    private String receiverPhone;
    private String shippingAddress;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<OrderItem> items;
    private Delivery deliveryInfo;

    // 构造方法
    public Order() {}

    public Order(String orderId, String customerId, String username, BigDecimal totalAmount, Integer status) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.username = username;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    // getter和setter
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public Delivery getDeliveryInfo() {
        return deliveryInfo;
    }

    public void setDeliveryInfo(Delivery deliveryInfo) {
        this.deliveryInfo = deliveryInfo;
    }
} 