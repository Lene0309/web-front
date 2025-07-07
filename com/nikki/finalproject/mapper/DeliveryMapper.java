package com.nikki.finalproject.mapper;

import com.nikki.finalproject.entity.Delivery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DeliveryMapper {
    
    /**
     * 根据订单ID查询配送信息
     */
    Delivery selectByOrderId(@Param("orderId") String orderId);

    /**
     * 插入配送信息
     */
    int insert(Delivery delivery);

    /**
     * 更新配送信息
     */
    int update(Delivery delivery);

    /**
     * 查询配送列表
     */
    List<Delivery> selectDeliveryList(@Param("orderId") String orderId,
                                     @Param("deliveryNumber") String deliveryNumber,
                                     @Param("deliveryCompany") String deliveryCompany,
                                     @Param("deliveryStatus") Integer deliveryStatus,
                                     @Param("offset") int offset,
                                     @Param("pageSize") int pageSize);

    /**
     * 统计配送总数
     */
    int countDeliveryList(@Param("orderId") String orderId,
                         @Param("deliveryNumber") String deliveryNumber,
                         @Param("deliveryCompany") String deliveryCompany,
                         @Param("deliveryStatus") Integer deliveryStatus);
} 