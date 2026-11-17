package com.chx.saleswithagent.Controller;

import com.chx.saleswithagent.DTO.LoginDTO;
import com.chx.saleswithagent.DTO.RegisterDTO;
import com.chx.saleswithagent.Result.Result;
import com.chx.saleswithagent.Service.LoginService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class LoginController {
    @Resource
    private LoginService loginService;

    /*
    *注册功能的实现
    *
     */
    @PostMapping("/register")
    public Result register(@RequestBody RegisterDTO registerDTO){
        return Result.ok(loginService.register(registerDTO));
    }
}
