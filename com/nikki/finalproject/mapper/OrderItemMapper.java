package com.nikki.finalproject.mapper;

import com.nikki.finalproject.entity.OrderItem;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface OrderItemMapper {

    /**
     * 根据订单ID查询商品项
     */

    @Select("SELECT " +
            "oi.item_id AS itemId, " +
            "oi.order_id AS orderId, " +
            "oi.product_id AS productId, " +
            "oi.product_name AS productName, " +
            "p.image_url AS productImage, " +
            "oi.current_price AS currentPrice, " +
            "oi.quantity, " +
            "oi.total_price AS totalPrice, " +
            "c.category_name AS categoryName " +
            "FROM t_order_item oi " +
            "LEFT JOIN products p ON oi.product_id = p.product_id " +
            "LEFT JOIN categories c ON p.category_id = c.category_id " +
            "WHERE oi.order_id = #{orderId}")
    @Results({
            @Result(property = "itemId", column = "itemId"),
            @Result(property = "orderId", column = "orderId"),
            @Result(property = "productId", column = "productId"),
            @Result(property = "productName", column = "productName"),
            @Result(property = "productImage", column = "productImage"),
            @Result(property = "currentPrice", column = "currentPrice", javaType = BigDecimal.class),
            @Result(property = "quantity", column = "quantity", javaType = Integer.class),
            @Result(property = "totalPrice", column = "totalPrice", javaType = BigDecimal.class),
            @Result(property = "categoryName", column = "categoryName")
    })
    List<OrderItem> selectItemsByOrderId(@Param("orderId") String orderId);

    /**
     * 插入单个订单项
     */
    @Insert("INSERT INTO t_order_item (" +
            "item_id, order_id, product_id, product_name, product_image, " +
            "current_price, quantity, total_price" +
            ") VALUES (" +
            "#{itemId}, #{orderId}, #{productId}, #{productName}, #{productImage}, " +
            "#{currentPrice}, #{quantity}, #{totalPrice}" +
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "itemId")
    int insert(OrderItem item);

    /**
     * 批量插入订单项
     */
    @Insert("<script>" +
            "INSERT INTO t_order_item (" +
            "item_id, order_id, product_id, product_name, product_image, " +
            "current_price, quantity, total_price" +
            ") VALUES " +
            "<foreach collection='items' item='item' separator=','>" +
            "(#{item.itemId}, #{item.orderId}, #{item.productId}, #{item.productName}, #{item.productImage}, " +
            "#{item.currentPrice}, #{item.quantity}, #{item.totalPrice})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("items") List<OrderItem> items);

    /**
     * 根据订单ID删除所有订单项
     */
    @Delete("DELETE FROM t_order_item WHERE order_id = #{orderId}")
    int deleteByOrderId(@Param("orderId") String orderId);

    /**
     * 根据订单项ID删除单个订单项
     */
    @Delete("DELETE FROM t_order_item WHERE item_id = #{itemId}")
    int deleteById(@Param("itemId") Long itemId);

    /**
     * 更新订单项信息
     */
    @Update("UPDATE t_order_item SET " +
            "product_id = #{productId}, " +
            "product_name = #{productName}, " +
            "product_image = #{productImage}, " +
            "current_price = #{currentPrice}, " +
            "quantity = #{quantity}, " +
            "total_price = #{totalPrice} " +
            "WHERE item_id = #{itemId}")
    int update(OrderItem item);
} 