package com.chx.saleswithagent.Controller;

import com.chx.saleswithagent.Common.CurrentUser;
import com.chx.saleswithagent.Result.Result;
import com.chx.saleswithagent.DTO.AllSelectedDTO;
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
        return Result.ok(buildCart(CurrentUser.id()));
    }
    /*
    *添加商品进购物车
    *
     */
    @PostMapping("/items")
    public Result<CartVO> addItem(@Valid @RequestBody CartItemDTO cartItemDTO){
        log.info("添加购物车数据");
        Long userId = CurrentUser.id();
        cartService.addItem(userId, cartItemDTO);
        return Result.ok(buildCart(userId));
    }
    /*
    *更新购物车数据（数量(加减购物车数量)，勾选状态）
    *
     */
    @PutMapping("/items/{itemId}")
    public Result<CartVO> updateItem(@PathVariable Long itemId,
                                     @Valid @RequestBody CartItemUpdateDTO dto){
        log.info("更新购物车数据");
        Long userId = CurrentUser.id();
        cartService.updateItem(userId, itemId, dto);
        return Result.ok(buildCart(userId));
    }
    /*
    *删除购物车商品
    *
     */
    @DeleteMapping("/items/{itemId}")
    public Result<CartVO> deleteItem(@PathVariable Long itemId){
        log.info("删除商品");
        Long userId = CurrentUser.id();
        cartService.deleteItem(userId, itemId);
        return Result.ok(buildCart(userId));
    }
    /*
    *在购物车页面点击再次购买
    *
     */
    @PostMapping("/reorder/{itemId}")
    public Result reorder(@PathVariable Long itemId){
        return Result.ok(cartService.reorder(itemId));
    }
    /*
    *返回购物车当前商品数量,用作购物车角标
    *
    *
     */
    @GetMapping("/count")
    public Result count(){
        return Result.ok(cartService.count(CurrentUser.id()));
    }
    /*
    *处理购物车处点击全选
    *
     */
    @PutMapping("/selected")
    public Result<CartVO> allSelected(@RequestBody AllSelectedDTO allSelectedDTO){
        Long userId = CurrentUser.id();
        cartService.allSelected(userId, allSelectedDTO);
        return Result.ok(buildCart(userId));
    }
    /*
    *删除选中商品
    *
     */
    @DeleteMapping("/selected")
    public Result<CartVO> deleteSelected(){
        Long userId = CurrentUser.id();
        cartService.deleteSelected(userId);
        return Result.ok(buildCart(userId));
    }

    /* 统一构建购物车返回：条目 + 总件数 + 选中金额 */
    private CartVO buildCart(Long userId) {
        List<CartItemVO> items = cartService.list(userId);
        CartVO vo = new CartVO();
        vo.setItems(items);
        vo.setTotalQuantity(items.stream()
                .mapToInt(CartItemVO::getQuantity).sum());
        vo.setSelectedAmount(items.stream()
                .filter(CartItemVO::isSelected)
                .map(CartItemVO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return vo;
    }

}
