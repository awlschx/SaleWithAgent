package com.chx.saleswithagent.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品接口（前后端联调第一条线）。
 *
 * 前端通过 GET /api/products 拉取商品列表；
 * 静态页面由 Spring Boot 从 classpath:/static 提供，同源无需处理跨域。
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> list() {
        return productService.list();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> detail(@PathVariable long id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
