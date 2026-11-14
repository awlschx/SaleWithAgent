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
    /*
    *编辑收货地址
    *
     */
    @PutMapping("/{id}")
    public Result updateAddress(@PathVariable Long id,@RequestBody AddressDTO addressDTO){
        return Result.ok(addressService.updateAddress(id,addressDTO));
    }
    /*
    *删除收货地址
    *
     */
    @DeleteMapping("/{id}")
    public Result deleteAddressById(@PathVariable Long id){
        return Result.ok(addressService.deleteAddressById(id));
    }
}
