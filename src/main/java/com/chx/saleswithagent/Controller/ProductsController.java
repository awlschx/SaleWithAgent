package com.chx.saleswithagent.Controller;

import com.chx.saleswithagent.Common.UserContext;
import com.chx.saleswithagent.DTO.ProductQueryDTO;
import com.chx.saleswithagent.Result.PageResult;
import com.chx.saleswithagent.Result.Result;
import com.chx.saleswithagent.Service.ProductsService;
import com.chx.saleswithagent.VO.ProductsVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
//    @GetMapping
//    public Result<List<ProductsVO>> ListProducts(){
//        log.info("获取products");
//        Long userId = UserContext.getUserId();
//        return Result.ok(productsService.ListProducts());
//    }
    /*
    *商品分类功能实现
     */
    @GetMapping
    public Result<PageResult<ProductsVO>> pageProducts( ProductQueryDTO productQueryDTO){
        return  Result.ok(productsService.pageProduct(productQueryDTO));
    }
    /*
    *秒杀分类的实现
     */
    @GetMapping("/flash")
    public Result<PageResult<ProductsVO>> flashProducts(ProductQueryDTO productQueryDTO){
        return Result.ok(productsService.flashProducts(productQueryDTO));
    }
}
