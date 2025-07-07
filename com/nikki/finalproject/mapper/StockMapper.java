package com.nikki.finalproject.mapper;

import com.nikki.finalproject.entity.Stock;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface StockMapper {
    
    // 获取库存列表
    @Select("<script>" +
            "SELECT " +
            "  p.product_id AS productId, " +
            "  p.product_name AS productName, " +
            "  p.image_url AS imageUrl, " +
            "  p.category_id AS categoryId, " +
            "  c.category_name AS categoryName, " +
            "  p.price, " +
            "  p.stock, " +
            "  p.status, " +
            "  p.created_at AS createdAt, " +
            "  p.updated_at AS updatedAt " +
            "FROM products p " +
            "LEFT JOIN categories c ON p.category_id = c.category_id " +
            "<where>" +
            "   <if test='keyword != null'>AND p.product_name LIKE CONCAT('%', #{keyword}, '%')</if>" +
            "   <if test='categoryId != null'>AND p.category_id = #{categoryId}</if>" +
            "   <if test='status != null'>AND p.status = #{status}</if>" +
            "   <if test='minPrice != null'>AND p.price &gt;= #{minPrice}</if>" +
            "   <if test='maxPrice != null'>AND p.price &lt;= #{maxPrice}</if>" +
            "   <if test='minStock != null'>AND p.stock &gt;= #{minStock}</if>" +
            "   <if test='maxStock != null'>AND p.stock &lt;= #{maxStock}</if>" +
            "   <if test='stockFilter != null'>" +
            "       <choose>" +
            "           <when test='stockFilter == \"low\"'>AND p.stock &lt; 10</when>" +
            "           <when test='stockFilter == \"normal\"'>AND p.stock BETWEEN 10 AND 50</when>" +
            "           <when test='stockFilter == \"high\"'>AND p.stock &gt; 50</when>" +
            "       </choose>" +
            "   </if>" +
            "</where>" +
            "ORDER BY p.created_at DESC " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<Stock> selectStockList(Map<String, Object> params);
    
    // 统计库存总数
    @Select("<script>" +
            "SELECT COUNT(*) FROM products p " +
            "<where>" +
            "   <if test='keyword != null'>AND p.product_name LIKE CONCAT('%', #{keyword}, '%')</if>" +
            "   <if test='categoryId != null'>AND p.category_id = #{categoryId}</if>" +
            "   <if test='status != null'>AND p.status = #{status}</if>" +
            "   <if test='minPrice != null'>AND p.price &gt;= #{minPrice}</if>" +
            "   <if test='maxPrice != null'>AND p.price &lt;= #{maxPrice}</if>" +
            "   <if test='minStock != null'>AND p.stock &gt;= #{minStock}</if>" +
            "   <if test='maxStock != null'>AND p.stock &lt;= #{maxStock}</if>" +
            "   <if test='stockFilter != null'>" +
            "       <choose>" +
            "           <when test='stockFilter == \"low\"'>AND p.stock &lt; 10</when>" +
            "           <when test='stockFilter == \"normal\"'>AND p.stock BETWEEN 10 AND 50</when>" +
            "           <when test='stockFilter == \"high\"'>AND p.stock &gt; 50</when>" +
            "       </choose>" +
            "   </if>" +
            "</where>" +
            "</script>")
    int countStockList(Map<String, Object> params);
    
    // 获取分类列表
    @Select("SELECT * FROM categories")
    List<Map<String, Object>> selectCategories();
    
    // 更新库存
    @Update("UPDATE products SET " +
            "stock = #{stock}, " +
            "updated_at = CURRENT_TIMESTAMP " +
            "WHERE product_id = #{productId}")
    int updateStock(@Param("productId") String productId, @Param("stock") Integer stock);
    
    // 更新商品状态
    @Update("UPDATE products SET status = #{status}, updated_at = CURRENT_TIMESTAMP WHERE product_id = #{productId}")
    int updateStockStatus(@Param("productId") String productId, @Param("status") Integer status);
    
    // 单个删除商品
    @Delete("DELETE FROM products WHERE product_id = #{productId}")
    int deleteStock(@Param("productId") String productId);
    
    // 批量删除商品
    @Delete("<script>" +
            "DELETE FROM products WHERE product_id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchDeleteStock(@Param("ids") List<String> ids);
    
    // 添加商品
    @Insert("INSERT INTO products (product_id, product_name, category_id, price, stock, status, image_url, created_at, updated_at) " +
            "VALUES (#{productId}, #{productName}, #{categoryId}, #{price}, #{stock}, #{status}, #{imageUrl}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
    int addStock(Stock stock);
    
    // 更新商品信息
    @Update("UPDATE products SET " +
            "product_name = #{productName}, " +
            "category_id = #{categoryId}, " +
            "price = #{price}, " +
            "stock = #{stock}, " +
            "status = #{status}, " +
            "image_url = #{imageUrl}, " +
            "updated_at = CURRENT_TIMESTAMP " +
            "WHERE product_id = #{productId}")
    int updateProduct(Stock stock);
    
    // 根据ID获取商品
    @Select("SELECT " +
            "  p.product_id AS productId, " +
            "  p.product_name AS productName, " +
            "  p.image_url AS imageUrl, " +
            "  p.category_id AS categoryId, " +
            "  c.category_name AS categoryName, " +
            "  p.price, " +
            "  p.stock, " +
            "  p.status, " +
            "  p.created_at AS createdAt, " +
            "  p.updated_at AS updatedAt " +
            "FROM products p " +
            "LEFT JOIN categories c ON p.category_id = c.category_id " +
            "WHERE p.product_id = #{productId}")
    Stock selectStockById(@Param("productId") String productId);
} 