package com.chx.saleswithagent.product;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 商品服务（第一版：内存演示数据，后续替换为 MySQL 查询）。
 *
 * 数据与前端内置演示数据保持一致，方便对比"接口通了没有"；
 * id=17 是专门加的联调验证商品，首页能看到它即说明数据来自后端。
 */
@Service
public class ProductService {

    private final List<Product> products = new ArrayList<>();

    public ProductService() {
        seed();
    }

    public List<Product> list() {
        return List.copyOf(products);
    }

    public Optional<Product> findById(long id) {
        return products.stream().filter(p -> p.id() == id).findFirst();
    }

    private void seed() {
        add(1, "纯棉宽松短袖T恤 男女同款", "服饰", 59, 99, 12800, 20);
        add(2, "防风连帽冲锋衣 秋冬加厚", "服饰", 259, 459, 5600, 210);
        add(3, "无线蓝牙耳机 主动降噪", "数码", 169, 299, 23400, 215);
        add(4, "65W 氮化镓快充套装", "数码", 79, 129, 18900, 200);
        add(5, "机械键盘 87键热插拔", "数码", 229, 329, 7300, 250);
        add(6, "智能手环 心率血氧监测", "数码", 129, 199, 9800, 160);
        add(7, "全棉四件套 亲肤磨毛", "家居", 189, 359, 6100, 35);
        add(8, "北欧风落地台灯", "家居", 119, 189, 3200, 45);
        add(9, "304 不锈钢保温杯 500ml", "家居", 49, 89, 31000, 190);
        add(10, "氨基酸洁面乳 温和清洁", "美妆", 39, 69, 42000, 340);
        add(11, "保湿面霜 修护屏障", "美妆", 129, 219, 8800, 10);
        add(12, "每日坚果礼盒 30 包装", "食品", 69, 109, 52000, 30);
        add(13, "云南小粒咖啡豆 454g", "食品", 59, 88, 15600, 25);
        add(14, "轻量缓震跑鞋", "运动", 299, 499, 11200, 150);
        add(15, "加厚防滑瑜伽垫", "运动", 59, 99, 20500, 120);
        add(16, "可调节哑铃 一对", "运动", 169, 259, 4300, 0);
        // 联调验证商品：首页出现这一件，就证明商品列表真的来自 /api/products
        add(17, "拾光联名帆布袋（后端联调验证）", "服饰", 39, 59, 999, 120);
    }

    private void add(long id, String name, String category, long price, long originalPrice,
                     long sales, int hue) {
        long flashPrice = Math.round(price * 0.8);
        int flashPercent = 35 + (int) ((id * 13) % 60);
        products.add(new Product(id, name, category, price, originalPrice, sales, hue,
                flashPrice, flashPercent));
    }
}
