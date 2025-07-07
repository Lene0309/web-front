package com.nikki.finalproject.biz;

import com.nikki.finalproject.entity.Stock;
import com.nikki.finalproject.mapper.StockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import java.util.Arrays;

@Service
public class StockBiz {
    @Autowired
    private StockMapper stockMapper;

    // 获取库存列表
    public Map<String, Object> getStockList(Integer pageNum, Integer pageSize, String keyword, Integer categoryId,
                                            Integer status, BigDecimal minPrice, BigDecimal maxPrice,
                                            Integer minStock, Integer maxStock, String stockFilter) {
        Map<String, Object> params = new HashMap<>();
        params.put("offset", (pageNum - 1) * pageSize);
        params.put("pageSize", pageSize);
        params.put("keyword", keyword);
        params.put("categoryId", categoryId);
        params.put("status", status);
        params.put("minPrice", minPrice);
        params.put("maxPrice", maxPrice);
        params.put("minStock", minStock);
        params.put("maxStock", maxStock);
        params.put("stockFilter", stockFilter);

        List<Stock> list = stockMapper.selectStockList(params);
        int total = stockMapper.countStockList(params);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        return result;
    }

    // 获取分类列表
    public List<Map<String, Object>> getCategories() {
        return stockMapper.selectCategories();
    }

    // 更新库存
    public int updateStock(String productId, Integer stock) {
        return stockMapper.updateStock(productId, stock);
    }

    // 更新商品状态
    public int updateStockStatus(String productId, Integer status) {
        return stockMapper.updateStockStatus(productId, status);
    }

    // 批量删除商品
    public int batchDeleteStock(List<String> ids) {
        return stockMapper.batchDeleteStock(ids);
    }

    // 单个删除商品
    public int deleteStock(String productId) {
        return stockMapper.deleteStock(productId);
    }

    // 添加商品
    public int addStock(Stock stock) {
        // 生成唯一的商品ID
        String productId = generateProductId();
        stock.setProductId(productId);
        
        // 设置默认值
        if (stock.getStatus() == null) {
            stock.setStatus(1); // 默认上架
        }
        if (stock.getStock() == null) {
            stock.setStock(0); // 默认库存为0
        }
        
        return stockMapper.addStock(stock);
    }
    
    // 生成唯一的商品ID
    private String generateProductId() {
        // 获取当前时间戳
        long timestamp = System.currentTimeMillis();
        // 生成6位随机数
        int random = (int) (Math.random() * 900000) + 100000;
        // 组合成商品ID：P + 时间戳后8位 + 6位随机数
        String productId = "P" + String.valueOf(timestamp).substring(5) + random;
        return productId;
    }

    // 编辑商品
    public int updateProduct(Stock stock) {
        return stockMapper.updateProduct(stock);
    }

    // 获取库存报表数据
    public Map<String, Object> getStockReport() {
        Map<String, Object> report = new HashMap<>();

        // 获取所有库存数据
        Map<String, Object> params = new HashMap<>();
        params.put("offset", 0);
        params.put("pageSize", 10000); // 获取所有数据
        List<Stock> allStocks = stockMapper.selectStockList(params);

        int totalProducts = allStocks.size();
        int lowStockProducts = 0;
        int outOfStockProducts = 0;
        BigDecimal totalValue = BigDecimal.ZERO;
        List<Map<String, Object>> lowStockList = new ArrayList<>();

        for (Stock stock : allStocks) {
            // 计算库存价值
            BigDecimal stockValue = stock.getPrice().multiply(new BigDecimal(stock.getStock()));
            totalValue = totalValue.add(stockValue);

            // 统计低库存和缺货商品
            if (stock.getStock() == 0) {
                outOfStockProducts++;
            } else if (stock.getStock() < 10) {
                lowStockProducts++;
            }

            // 添加低库存商品到列表
            if (stock.getStock() < 10) {
                Map<String, Object> lowStockItem = new HashMap<>();
                lowStockItem.put("productId", stock.getProductId());
                lowStockItem.put("productName", stock.getProductName());
                lowStockItem.put("categoryName", stock.getCategoryName());
                lowStockItem.put("currentStock", stock.getStock());
                lowStockItem.put("price", stock.getPrice());
                lowStockItem.put("stockValue", stockValue);
                lowStockList.add(lowStockItem);
            }
        }

        report.put("totalProducts", totalProducts);
        report.put("lowStockProducts", lowStockProducts);
        report.put("outOfStockProducts", outOfStockProducts);
        report.put("totalValue", totalValue);
        report.put("lowStockList", lowStockList);

        return report;
    }

    // 导出库存报表
    public void exportStockReport(HttpServletResponse response) {
        try {
            // 设置响应头
            response.setContentType("text/csv;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=stock_report_" +
                    new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".csv");

            // 获取报表数据
            Map<String, Object> reportData = getStockReport();

            // 写入CSV内容
            OutputStream outputStream = response.getOutputStream();

            // 写入BOM，解决中文乱码
            outputStream.write(0xEF);
            outputStream.write(0xBB);
            outputStream.write(0xBF);

            // 写入表头
            String header = "商品ID,商品名称,分类,当前库存,单价,库存价值,库存状态\n";
            outputStream.write(header.getBytes(StandardCharsets.UTF_8));

            // 获取所有库存数据
            Map<String, Object> params = new HashMap<>();
            params.put("offset", 0);
            params.put("pageSize", 10000);
            List<Stock> allStocks = stockMapper.selectStockList(params);

            // 写入数据行
            for (Stock stock : allStocks) {
                BigDecimal stockValue = stock.getPrice().multiply(new BigDecimal(stock.getStock()));
                String stockStatus = getStockStatusText(stock.getStock());

                String row = String.format("%s,%s,%s,%d,%.2f,%.2f,%s\n",
                        stock.getProductId(),
                        stock.getProductName(),
                        stock.getCategoryName() != null ? stock.getCategoryName() : "",
                        stock.getStock(),
                        stock.getPrice(),
                        stockValue,
                        stockStatus);

                outputStream.write(row.getBytes(StandardCharsets.UTF_8));
            }

            // 写入汇总信息
            outputStream.write("\n".getBytes(StandardCharsets.UTF_8));
            outputStream.write("汇总信息\n".getBytes(StandardCharsets.UTF_8));
            outputStream.write(String.format("总商品数,%d\n", reportData.get("totalProducts")).getBytes(StandardCharsets.UTF_8));
            outputStream.write(String.format("低库存商品数,%d\n", reportData.get("lowStockProducts")).getBytes(StandardCharsets.UTF_8));
            outputStream.write(String.format("缺货商品数,%d\n", reportData.get("outOfStockProducts")).getBytes(StandardCharsets.UTF_8));
            outputStream.write(String.format("总库存价值,%.2f\n", reportData.get("totalValue")).getBytes(StandardCharsets.UTF_8));

            outputStream.flush();
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 获取库存状态文本
    private String getStockStatusText(Integer stock) {
        if (stock == 0) {
            return "缺货";
        } else if (stock < 10) {
            return "低库存";
        } else if (stock < 50) {
            return "正常";
        } else {
            return "充足";
        }
    }
} 