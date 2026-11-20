package com.chx.saleswithagent.Service.Impl;

import com.chx.saleswithagent.Common.UserContext;
import com.chx.saleswithagent.DTO.LoginDTO;
import com.chx.saleswithagent.DTO.RegisterDTO;
import com.chx.saleswithagent.Repository.LoginRepository;
import com.chx.saleswithagent.Service.LoginService;
import com.chx.saleswithagent.VO.UserVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
}
