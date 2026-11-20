package com.chx.saleswithagent.Controller;

import com.chx.saleswithagent.Common.UserContext;
import com.chx.saleswithagent.DTO.OrdersDTO;
import com.chx.saleswithagent.Result.Result;
import com.chx.saleswithagent.Service.CouponsService;
import com.chx.saleswithagent.Service.OrdersService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/orders")
public class OrdersController {


    @Resource
    private OrdersService ordersService;

    @Resource
    private CouponsService couponsService;

    /*
    *页面加载时获取订单
    *
     */
    @GetMapping
    public Result getOrders(@RequestParam(defaultValue = "all") String status){
        Long userId = UserContext.getUserId();
        return Result.ok(ordersService.getOrders(userId,status));
    }
    /*
    *提交订单
    *
     */
    @PostMapping
    public Result addOrders(@RequestBody OrdersDTO ordersDTO){
        log.info("添加订单");
        log.info(ordersDTO.toString());
        Long userId = UserContext.getUserId();
        return Result.ok(ordersService.addOrders(ordersDTO,userId));
    }

    /*
    *取消订单
    *
     */
    @PostMapping("/{orderNo}/cancel")
    public Result cancelOrders(@PathVariable String orderNo){

        return Result.ok(ordersService.cancelOrders(orderNo));
    }
    /*
    *模拟发货
    *
     */
    @PostMapping("/{orderNo}/ship")
    public Result shipOrders(@PathVariable String orderNo){

        return Result.ok(ordersService.shipOrders(orderNo));
    }

    /*
    *确认收货
    *
    *
     */
    @PostMapping("/{orderNo}/receive")
    public Result receiveOrders(@PathVariable String orderNo){
        return Result.ok(ordersService.receiveOrders(orderNo));
    }
}
