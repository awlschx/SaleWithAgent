package com.chx.saleswithagent.Controller;

import com.chx.saleswithagent.Common.UserContext;
import com.chx.saleswithagent.Result.Result;
import com.chx.saleswithagent.DTO.AllSelectedDTO;
import com.chx.saleswithagent.DTO.CartItemDTO;
import com.chx.saleswithagent.DTO.CartItemUpdateDTO;
import com.chx.saleswithagent.Service.CartService;
import com.chx.saleswithagent.VO.CartVO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/cart")
public class CartController {
    Long userId = UserContext.getUserId();
    //注入cartservice
    @Resource
    private CartService cartService;
    //从后端获取数据库表的购物车的数据
    @GetMapping
    public Result<CartVO> list(){
        log.info("获取购物车数据");

        return Result.ok(cartService.buildCart(userId));
    }
    /*
    *添加商品进购物车
    *
     */
    @PostMapping("/items")
    public Result<CartVO> addItem(@Valid @RequestBody CartItemDTO cartItemDTO){
        log.info("添加购物车数据");
        cartService.addItem(userId, cartItemDTO);
        return Result.ok(cartService.buildCart(userId));
    }
    /*
    *更新购物车数据（数量(加减购物车数量)，勾选状态）
    *
     */
    @PutMapping("/items/{itemId}")
    public Result<CartVO> updateItem(@PathVariable Long itemId,
                                     @Valid @RequestBody CartItemUpdateDTO dto){
        log.info("更新购物车数据");
        cartService.updateItem(userId, itemId, dto);
        return Result.ok(cartService.buildCart(userId));
    }
    /*
    *删除购物车商品
    *
     */
    @DeleteMapping("/items/{itemId}")
    public Result<CartVO> deleteItem(@PathVariable Long itemId){
        log.info("删除商品");
        cartService.deleteItem(userId, itemId);
        return Result.ok(cartService.buildCart(userId));
    }
    /*
    *在购物车页面点击再次购买
    *
     */
    @PostMapping("/reorder/{orderNo}")
    public Result reorder(@PathVariable String orderNo){
        return Result.ok(cartService.reorder(orderNo,userId));
    }
    /*
    *返回购物车当前商品数量,用作购物车角标
    *
    *
     */
    @GetMapping("/count")
    public Result count(){
        return Result.ok(cartService.count(userId));
    }
    /*
    *处理购物车处点击全选
    *
     */
    @PutMapping("/selected")
    public Result<CartVO> allSelected(@RequestBody AllSelectedDTO allSelectedDTO){
        cartService.allSelected(userId, allSelectedDTO);
        return Result.ok(cartService.buildCart(userId));
    }
    /*
    *删除选中商品
    *
     */
    @DeleteMapping("/selected")
    public Result<CartVO> deleteSelected(){
        cartService.deleteSelected(userId);
        return Result.ok(cartService.buildCart(userId));
    }



}
