package com.chx.saleswithagent.Service;

import com.chx.saleswithagent.DTO.LoginDTO;
import com.chx.saleswithagent.DTO.RegisterDTO;
import com.chx.saleswithagent.VO.LoginVO;
import com.chx.saleswithagent.VO.UserVO;

import java.util.Map;

public interface LoginService {
    UserVO register(RegisterDTO registerDTO);

    UserVO login(LoginDTO loginDTO);

    Map logout(String authorization);

    LoginVO getCurrentUser(long userId);
}
