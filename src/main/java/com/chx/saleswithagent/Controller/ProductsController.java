package com.chx.saleswithagent.Controller;

import com.chx.saleswithagent.Result.Result;
import com.chx.saleswithagent.Service.ProductsService;
import com.chx.saleswithagent.VO.ProductsVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
*商品的controller层
*
*
*/
@Slf4j
@RestController
@RequestMapping("/api/products")
public class ProductsController {
    @Resource
    private ProductsService productsService;
    /*
    *获取商品展示
    *
     */
    @GetMapping
    public Result<List<ProductsVO>> ListProducts(){
        log.info("获取products");
        return Result.ok(productsService.ListProducts());
    }
}
