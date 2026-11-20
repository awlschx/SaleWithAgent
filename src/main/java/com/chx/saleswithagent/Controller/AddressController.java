package com.chx.saleswithagent.Controller;

import com.chx.saleswithagent.Common.UserContext;
import com.chx.saleswithagent.DTO.AddressDTO;
import com.chx.saleswithagent.Result.Result;
import com.chx.saleswithagent.Service.AddressService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {
    Long userId = UserContext.getUserId();
    //注入service
    @Resource
    private AddressService addressService;
    /*
    *新增地址
    *
     */
    @PostMapping
    public Result addAddress(@RequestBody AddressDTO addressDTO){

        addressDTO.setUserId(userId);
        return Result.ok(addressService.addAddress(addressDTO));
    }

    /*
    *获取地址
    *
     */
    @GetMapping
    public Result getAddress(){
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
