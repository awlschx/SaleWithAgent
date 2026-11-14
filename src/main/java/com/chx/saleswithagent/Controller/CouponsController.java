package com.chx.saleswithagent.Controller;

import com.chx.saleswithagent.Result.Result;
import com.chx.saleswithagent.Service.AddressService;
import com.chx.saleswithagent.Service.CouponsService;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
