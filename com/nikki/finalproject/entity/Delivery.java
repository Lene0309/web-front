package com.nikki.finalproject.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Delivery implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String deliveryId;
    private String orderId;
    private String deliveryCompany;
    private String deliveryNumber;
    private Integer deliveryStatus; // 0:待发货 1:已发货 2:已签收
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 构造方法
    public Delivery() {}

    public Delivery(String deliveryId, String orderId, String deliveryCompany, 
                   String deliveryNumber, Integer deliveryStatus) {
        this.deliveryId = deliveryId;
        this.orderId = orderId;
        this.deliveryCompany = deliveryCompany;
        this.deliveryNumber = deliveryNumber;
        this.deliveryStatus = deliveryStatus;
    }

    // getter和setter
    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDeliveryCompany() {
        return deliveryCompany;
    }

    public void setDeliveryCompany(String deliveryCompany) {
        this.deliveryCompany = deliveryCompany;
    }

    public String getDeliveryNumber() {
        return deliveryNumber;
    }

    public void setDeliveryNumber(String deliveryNumber) {
        this.deliveryNumber = deliveryNumber;
    }

    public Integer getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(Integer deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
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
} 