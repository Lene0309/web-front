package com.nikki.finalproject.mapper;

import com.nikki.finalproject.entity.Order;
import com.nikki.finalproject.entity.OrderDelivery;
import com.nikki.finalproject.entity.OrderItem;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.math.BigDecimal;
import java.util.Date;

@Mapper
public interface OrderMapper {

    /**
     * 根据订单ID查询订单详情
     * 直接使用 t_order 表中的 username 字段
     */
    @Select("SELECT o.* FROM t_order o WHERE o.order_id = #{orderId}")
    @Results({
            @Result(property = "orderId", column = "order_id", id = true),
            @Result(property = "customerId", column = "customer_id"),
            @Result(property = "username", column = "username"),
            @Result(property = "totalAmount", column = "total_amount", javaType = BigDecimal.class),
            @Result(property = "status", column = "status", javaType = Integer.class),
            @Result(property = "createTime", column = "create_time", javaType = Date.class),
            @Result(property = "paymentTime", column = "payment_time", javaType = Date.class),
            @Result(property = "shippingTime", column = "shipping_time", javaType = Date.class),
            @Result(property = "completeTime", column = "complete_time", javaType = Date.class),
            @Result(property = "receiverName", column = "receiver_name"),
            @Result(property = "receiverPhone", column = "receiver_phone"),
            @Result(property = "shippingAddress", column = "shipping_address")
    })
    Order selectOrderById(@Param("orderId") String orderId);

    /**
     * 分页查询订单列表
     * 直接使用 t_order 表中的 username 字段
     */
    @Select("<script>" +
            "SELECT o.* FROM t_order o " +
            "<where>" +
            "  <if test='orderNo != null and orderNo != \"\"'>AND o.order_id LIKE CONCAT('%', #{orderNo}, '%')</if>" +
            "  <if test='username != null and username != \"\"'>AND o.username LIKE CONCAT('%', #{username}, '%')</if>" +
            "  <if test='status != null'>AND o.status = #{status}</if>" +
            "</where>" +
            "ORDER BY o.create_time DESC " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    @Results({
            @Result(property = "orderId", column = "order_id", id = true),
            @Result(property = "customerId", column = "customer_id"),
            @Result(property = "username", column = "username"),
            @Result(property = "totalAmount", column = "total_amount", javaType = BigDecimal.class),
            @Result(property = "status", column = "status", javaType = Integer.class),
            @Result(property = "createTime", column = "create_time", javaType = Date.class),
            @Result(property = "paymentTime", column = "payment_time", javaType = Date.class),
            @Result(property = "shippingTime", column = "shipping_time", javaType = Date.class),
            @Result(property = "completeTime", column = "complete_time", javaType = Date.class),
            @Result(property = "receiverName", column = "receiver_name"),
            @Result(property = "receiverPhone", column = "receiver_phone"),
            @Result(property = "shippingAddress", column = "shipping_address")
    })
    List<Order> selectOrderList(
            @Param("orderNo") String orderNo,
            @Param("username") String username,
            @Param("status") Integer status,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize);

    /**
     * 统计订单数量
     * 直接使用 t_order 表中的 username 字段
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM t_order o " +
            "<where>" +
            "  <if test='orderNo != null and orderNo != \"\"'>AND o.order_id LIKE CONCAT('%', #{orderNo}, '%')</if>" +
            "  <if test='username != null and username != \"\"'>AND o.username LIKE CONCAT('%', #{username}, '%')</if>" +
            "  <if test='status != null'>AND o.status = #{status}</if>" +
            "</where>" +
            "</script>")
    int countOrderList(
            @Param("orderNo") String orderNo,
            @Param("username") String username,
            @Param("status") Integer status);

    /**
     * 查询订单商品项（关联 products 表，适配字段）
     */
    @Select("SELECT oi.*, p.product_name, p.image_url, p.price " +
            "FROM t_order_item oi " +
            "LEFT JOIN products p ON oi.product_id = p.product_id " +
            "WHERE oi.order_id = #{orderId}")
    @Results({
            @Result(property = "itemId", column = "item_id", id = true),
            @Result(property = "orderId", column = "order_id"),
            @Result(property = "productId", column = "product_id"),
            @Result(property = "productName", column = "product_name"),
            @Result(property = "productImage", column = "image_url"),
            @Result(property = "currentPrice", column = "price", javaType = BigDecimal.class),
            @Result(property = "quantity", column = "quantity", javaType = Integer.class),
            @Result(property = "totalPrice", column = "total_price", javaType = BigDecimal.class)
    })
    List<OrderItem> selectItemsWithProductInfo(@Param("orderId") String orderId);

    /**
     * 查询订单物流信息
     */
    @Select("SELECT * FROM t_delivery WHERE order_id = #{orderId}")
    @Results({
            @Result(property = "deliveryId", column = "delivery_id", id = true),
            @Result(property = "orderId", column = "order_id"),
            @Result(property = "deliveryCompany", column = "delivery_company"),
            @Result(property = "deliveryNumber", column = "delivery_number"),
            @Result(property = "deliveryStatus", column = "delivery_status", javaType = Integer.class),
            @Result(property = "deliveryTime", column = "delivery_time", javaType = Date.class),
            @Result(property = "signTime", column = "sign_time", javaType = Date.class)
    })
    OrderDelivery selectByOrderId(@Param("orderId") String orderId);

    /**
     * 更新订单状态
     */
    @Update("UPDATE t_order SET status = #{status} WHERE order_id = #{orderId}")
    int updateOrderStatus(@Param("orderId") String orderId, @Param("status") Integer status);

    /**
     * 插入订单
     */
    @Insert("INSERT INTO t_order (" +
            "order_id, customer_id, username, total_amount, status, create_time, " +
            "payment_time, shipping_time, complete_time, " +
            "receiver_name, receiver_phone, shipping_address" +
            ") VALUES (" +
            "#{orderId}, #{customerId}, #{username}, #{totalAmount}, #{status}, #{createTime}, " +
            "#{paymentTime}, #{shippingTime}, #{completeTime}, " +
            "#{receiverName}, #{receiverPhone}, #{shippingAddress}" +
            ")")

    int insertOrder(Order order);

    /**
     * 更新订单
     */
    @Update("UPDATE t_order SET " +
            "total_amount = #{totalAmount}, " +
            "status = #{status}, " +
            "receiver_name = #{receiverName}, " +
            "receiver_phone = #{receiverPhone}, " +
            "shipping_address = #{shippingAddress}, " +
            "payment_time = #{paymentTime}, " +
            "shipping_time = #{shippingTime}, " +
            "complete_time = #{completeTime}, " +
            "update_time = #{updateTime} " +
            "WHERE order_id = #{orderId} AND customer_id = #{customerId}")
    int updateOrder(Order order);
    /**
     * 物理删除订单（先删订单项再删订单）
     */
    @Delete("DELETE FROM t_order WHERE order_id = #{orderId}")
    int deleteOrder(@Param("orderId") String orderId);
} 