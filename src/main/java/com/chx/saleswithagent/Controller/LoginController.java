package com.chx.saleswithagent.Controller;

import com.chx.saleswithagent.Common.UserContext;
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
        log.info("注册功能");
        return Result.ok(loginService.register(registerDTO));
    }
    /*
    *登录功能的实现
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO){
        log.info("登录功能");
        return Result.ok(loginService.login(loginDTO));
    }
    /*
    *登出功能的实现
     */
    @PostMapping("/logout")
    public Result logout(@RequestHeader("Authorization") String authorization){
        log.info("登出功能");
        return Result.ok(loginService.logout(authorization));
    }
}
