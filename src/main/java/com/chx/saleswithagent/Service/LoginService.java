package com.chx.saleswithagent.Service;

import com.chx.saleswithagent.DTO.LoginDTO;
import com.chx.saleswithagent.DTO.RegisterDTO;
import com.chx.saleswithagent.VO.UserVO;

public interface LoginService {
    UserVO register(RegisterDTO registerDTO);
}
