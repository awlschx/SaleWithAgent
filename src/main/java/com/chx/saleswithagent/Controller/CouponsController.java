package com.chx.saleswithagent.Controller;

import com.chx.saleswithagent.Common.CurrentUser;
import com.chx.saleswithagent.Result.Result;
import com.chx.saleswithagent.Service.AddressService;
import com.chx.saleswithagent.Service.CouponsService;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Data
@Slf4j
@RequestMapping("/api/coupons")
public class CouponsController {
    @Resource
    private CouponsService couponsService;

    /*
    *页面启动时加载优惠卷信息
    *
     */
     @GetMapping("/available")
    public Result listCoupons(){
         return Result.ok(couponsService. listCoupons());
     }
    /*
    *获取我的优惠卷
    *
     */
    @GetMapping("/mine")
    public Result mineCoupons(@RequestParam(defaultValue="all") String status){
        Long userId = CurrentUser.id();
        return Result.ok(couponsService.mineCoupons(userId,status));
    }
    /*
    *user领取优惠卷
    *
    *
     */
    @PostMapping("/{templateId}/claim")
    public Result clickCoupons(@PathVariable Long templateId){
        Long userId = CurrentUser.id();
        return Result.ok(couponsService.clickCoupons(userId,templateId));
    }
}
