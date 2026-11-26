package com.chx.saleswithagent.Service.Impl;

import com.chx.saleswithagent.Common.UserContext;
import com.chx.saleswithagent.DTO.LoginDTO;
import com.chx.saleswithagent.DTO.RegisterDTO;
import com.chx.saleswithagent.Repository.LoginRepository;
import com.chx.saleswithagent.Service.LoginService;
import com.chx.saleswithagent.VO.LoginVO;
import com.chx.saleswithagent.VO.UserVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    @Resource
    private LoginRepository loginRepository;
    @Override
    public UserVO register(RegisterDTO registerDTO) {
        return loginRepository.register(registerDTO);
    }

    @Override
    public UserVO login(LoginDTO loginDTO) {
        return loginRepository.login(loginDTO);
    }

    @Override
    public Map logout(String authorization) {
        String token = authorization.substring(7);
        boolean res = loginRepository.logout(token);
        Map<String ,Boolean> map = new HashMap<>();
        map.put("ok",res);
        return map;
    }

    @Override
    public LoginVO getCurrentUser(long userId) {
        return loginRepository.getCurrentUser(userId);
    }
}
