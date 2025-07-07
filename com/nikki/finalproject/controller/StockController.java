package com.nikki.finalproject.controller;

import com.nikki.finalproject.biz.StockBiz;
import com.nikki.finalproject.entity.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

@RestController
@RequestMapping("/stock")
public class StockController {
    @Autowired
    private StockBiz stockBiz;

    // 获取库存列表
    @GetMapping("/list")
    public Map<String, Object> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minStock,
            @RequestParam(required = false) Integer maxStock,
            @RequestParam(required = false) String stockFilter) {

        Map<String, Object> result = stockBiz.getStockList(
                pageNum, pageSize, keyword, categoryId, status,
                minPrice, maxPrice, minStock, maxStock, stockFilter);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", result);
        return response;
    }

    // 获取分类列表
    @GetMapping("/category/list")
    public Map<String, Object> categoryList() {
        List<Map<String, Object>> categories = stockBiz.getCategories();

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", categories);
        return response;
    }

    // 更新库存
    @PostMapping("/update/{productId}")
    public Map<String, Object> updateStock(@PathVariable String productId, @RequestParam Integer stock) {
        int result = stockBiz.updateStock(productId, stock);

        Map<String, Object> response = new HashMap<>();
        if (result > 0) {
            response.put("code", 200);
            response.put("message", "库存更新成功");
        } else {
            response.put("code", 500);
            response.put("message", "库存更新失败");
        }
        return response;
    }

    // 更新商品状态
    @PostMapping("/updateStatus/{productId}")
    public Map<String, Object> updateStatus(@PathVariable String productId, @RequestParam Integer status) {
        int result = stockBiz.updateStockStatus(productId, status);

        Map<String, Object> response = new HashMap<>();
        if (result > 0) {
            response.put("code", 200);
            response.put("message", "状态更新成功");
        } else {
            response.put("code", 500);
            response.put("message", "状态更新失败");
        }
        return response;
    }

    // 添加商品
    @PostMapping("/add")
    public Map<String, Object> addStock(@RequestBody Stock stock) {
        System.out.println("收到添加商品请求: " + stock);
        int result = stockBiz.addStock(stock);

        Map<String, Object> response = new HashMap<>();
        if (result > 0) {
            response.put("code", 200);
            response.put("message", "商品添加成功");
            response.put("data", stock.getProductId());
        } else {
            response.put("code", 500);
            response.put("message", "商品添加失败");
        }
        return response;
    }

    // 编辑商品
    @PostMapping("/edit")
    public Map<String, Object> editStock(@RequestBody Stock stock) {
        System.out.println("收到编辑商品请求: " + stock);
        int result = stockBiz.updateProduct(stock);

        Map<String, Object> response = new HashMap<>();
        if (result > 0) {
            response.put("code", 200);
            response.put("message", "商品编辑成功");
        } else {
            response.put("code", 500);
            response.put("message", "商品编辑失败");
        }
        return response;
    }

    // 批量删除商品
    @PostMapping("/batchDelete")
    public Map<String, Object> batchDelete(@RequestBody Map<String, List<String>> request) {
        List<String> ids = request.get("ids");
        int result = stockBiz.batchDeleteStock(ids);

        Map<String, Object> response = new HashMap<>();
        if (result > 0) {
            response.put("code", 200);
            response.put("message", "批量删除成功，共删除 " + result + " 条记录");
        } else {
            response.put("code", 500);
            response.put("message", "批量删除失败");
        }
        return response;
    }

    // 单个删除商品（POST方法，兼容前端）
    @PostMapping("/delete/{productId}")
    public Map<String, Object> deleteStockPost(@PathVariable String productId) {
        System.out.println("收到删除请求，productId: " + productId);
        int result = stockBiz.deleteStock(productId);
        System.out.println("删除结果: " + result);

        Map<String, Object> response = new HashMap<>();
        if (result > 0) {
            response.put("code", 200);
            response.put("message", "删除成功");
        } else {
            response.put("code", 500);
            response.put("message", "删除失败");
        }
        return response;
    }

    // 单个删除商品（DELETE方法）
    @DeleteMapping("/delete/{productId}")
    public Map<String, Object> deleteStock(@PathVariable String productId) {
        System.out.println("收到DELETE删除请求，productId: " + productId);
        int result = stockBiz.deleteStock(productId);
        System.out.println("删除结果: " + result);

        Map<String, Object> response = new HashMap<>();
        if (result > 0) {
            response.put("code", 200);
            response.put("message", "删除成功");
        } else {
            response.put("code", 500);
            response.put("message", "删除失败");
        }
        return response;
    }

    // 测试API是否正常工作
    @GetMapping("/test")
    public Map<String, Object> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "Stock API is working");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    // 获取库存报表数据
    @GetMapping("/report")
    public Map<String, Object> getStockReport() {
        Map<String, Object> reportData = stockBiz.getStockReport();

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", reportData);
        return response;
    }

    // 导出库存报表
    @GetMapping("/export")
    public void exportStockReport(HttpServletResponse response) {
        stockBiz.exportStockReport(response);
    }
} 