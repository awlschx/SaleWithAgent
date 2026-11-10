package com.chx.saleswithagent.product;

/**
 * 商品（演示数据模型）。
 *
 * 字段刻意保持"后端语义"，不照抄前端缩写：
 * name / category / price / originalPrice / sales / hue / flashPrice / flashPercent
 * 前端 api.js 负责把这份 JSON 映射成页面使用的 {n,c,p,o,s,h,f,pct}。
 */
public record Product(
        Long id,
        String name,
        String category,
        long price,
        long originalPrice,
        long sales,
        int hue,
        long flashPrice,
        int flashPercent) {
}
