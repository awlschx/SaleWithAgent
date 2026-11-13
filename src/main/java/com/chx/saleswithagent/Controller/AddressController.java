package com.chx.saleswithagent.Controller;

import com.chx.saleswithagent.Common.CurrentUser;
import com.chx.saleswithagent.DTO.AddressDTO;
import com.chx.saleswithagent.Result.Result;
import com.chx.saleswithagent.Service.AddressService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {
    //注入service
    @Resource
    private AddressService addressService;
    /*
    *新增地址
    *
     */
    @PostMapping
    public Result addAddress(@RequestBody AddressDTO addressDTO){
        addressDTO.setUserId(CurrentUser.id());
        return Result.ok(addressService.addAddress(addressDTO));
    }

    /*
    *获取地址
    *
     */
    @GetMapping
    public Result getAddress(){
        Long userId = CurrentUser.id();
        return Result.ok(addressService.getAddress(userId));
    }
}
