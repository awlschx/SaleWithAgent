package com.chx.saleswithagent.Controller;

import com.chx.saleswithagent.Common.CurrentUser;
import com.chx.saleswithagent.Common.Result;
import com.chx.saleswithagent.DTO.CartItemDTO;
import com.chx.saleswithagent.DTO.CartItemUpdateDTO;
import com.chx.saleswithagent.Service.CartService;
import com.chx.saleswithagent.VO.CartItemVO;
import com.chx.saleswithagent.VO.CartVO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/cart")
public class CartController {

    //注入cartservice
    @Resource
    private CartService cartService;
    //从后端获取数据库表的购物车的数据
    @GetMapping
    public Result<CartVO> list(){
        log.info("获取购物车数据");
        Long userId = CurrentUser.id();
        List<CartItemVO> items = cartService.list(userId);

        CartVO vo = new CartVO();
        vo.setItems(items);
        vo.setTotalQuantity(items.stream()
                .mapToInt(CartItemVO::getQuantity).sum());
        vo.setSelectedAmount(items.stream()
                .filter(CartItemVO::isSelected)
                .map(CartItemVO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        return Result.ok(vo);
    }
    /*
    *添加商品进购物车
    *
     */
    @PostMapping("/items")
    public Result addItem(@Valid @RequestBody CartItemDTO cartItemDTO){
        log.info("添加购物车数据");
        Long userId = CurrentUser.id();
        return Result.ok(cartService.addItem(userId,cartItemDTO));
    }
    /*
    *更新购物车数据（数量(加减购物车数量)，勾选状态）
    *
     */
    @PutMapping("/items/{productId}")
    public Result updateItem(@PathVariable Long productId,
                             @Valid @RequestBody CartItemUpdateDTO dto){
        log.info("更新购物车数据");
        return Result.ok(cartService.updateItem(productId, dto));
    }
    /*
    *删除购物车商品
    *
     */
    @DeleteMapping("/items/{productId}")
    public Result deleteItem(@PathVariable Long productId){
        return Result.ok(cartService.deleteItem(productId));
    }
    /*
    *在购物车页面点击再次购买
    *
     */
    @PostMapping("/reorder/{productId}")
    public Result reorder(@PathVariable Long productId){
        return Result.ok(cartService.reorder(productId));
    }
    /*
    *返回购物车当前商品数量,用作购物车角标
    *
    *
     */
    @GetMapping("/count")
    public Result count(){
        return Result.ok(cartService.count());
    }
}
