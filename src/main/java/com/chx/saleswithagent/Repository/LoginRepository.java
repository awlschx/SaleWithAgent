package com.chx.saleswithagent.Repository;


import com.chx.saleswithagent.DTO.LoginDTO;
import com.chx.saleswithagent.DTO.RegisterDTO;
import com.chx.saleswithagent.VO.UserVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@Slf4j
public class LoginRepository {
    @Resource
    private JdbcTemplate jdbc;
    @Resource
    private PasswordEncoder passwordEncoder;
    /*
    *用户注册
    *
     */
    public UserVO register(RegisterDTO registerDTO) {
        //能否注册成功要查users表，看看该用户是否存在，如果存在返回false，、
        //否则就可以注册
        //一个手机号只可以注册一个，nickname可以重复
        String existSql = "SELECT id FROM users WHERE phone = ?";
        int res = jdbc.update(existSql, registerDTO.getPhone());
        if (res > 0){
            return null;
        }
        String password = passwordEncoder.encode(registerDTO.getPassword());
        String insertSql = "INSERT INTO users (phone,password,nickname) VALUES (?,?,?) ";
        if(registerDTO.getNickname() == null){
            registerDTO.setNickname("妮露"+ System.currentTimeMillis());
        }
        jdbc.update(insertSql, registerDTO.getPhone(), password, registerDTO.getNickname());
        UserVO userVO = new UserVO();

    }
}
