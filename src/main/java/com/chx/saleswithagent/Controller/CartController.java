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
    @PutMapping("/items/{itemId}")
    public Result updateItem(@PathVariable Long itemId,
                             @Valid @RequestBody CartItemUpdateDTO dto){
        log.info("更新购物车数据");
        Long userId = CurrentUser.id();
        return Result.ok(cartService.updateItem(userId,itemId, dto));
    }
    /*
    *删除购物车商品
    *
     */
    @DeleteMapping("/items/{itemId}")
    public Result deleteItem(@PathVariable Long itemId){
        log.info("删除商品");
        Long userId = CurrentUser.id();
        return Result.ok(cartService.deleteItem(userId,itemId));
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
    public Result  allSelected(@RequestBody AllSelectedDTO allSelectedDTO){
        return Result.ok(cartService.allSelected(CurrentUser.id(),allSelectedDTO));
    }
    /*
    *删除选中商品
    *
     */
    @DeleteMapping("/selected")
    public Result deleteSelected(){
        return Result.ok(cartService.deleteSelected(CurrentUser.id()));
    }

}
