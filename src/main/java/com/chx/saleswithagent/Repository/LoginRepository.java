package com.chx.saleswithagent.Repository;


import com.chx.saleswithagent.DTO.LoginDTO;
import com.chx.saleswithagent.DTO.RegisterDTO;
import com.chx.saleswithagent.Security.JwtTokenService;
import com.chx.saleswithagent.VO.LoginVO;
import com.chx.saleswithagent.VO.UserVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@Slf4j
public class LoginRepository {
    @Autowired
    private JwtTokenService jwtTokenService;
    @Resource
    private JdbcTemplate jdbc;
    @Resource
    private PasswordEncoder passwordEncoder;
    /*
    *用户注册
    *
     */
    public UserVO register(RegisterDTO registerDTO) {
        log.info("111");
        //能否注册成功要查users表，看看该用户是否存在，如果存在返回false，、
        //否则就可以注册
        //一个手机号只可以注册一个，nickname可以重复
        String existSql = "SELECT EXISTS(" +
                "SELECT id FROM users WHERE phone = ?)";
        Boolean res = jdbc.queryForObject(existSql, Boolean.class,registerDTO.getPhone());
        log.info(String.valueOf(res));
        if (Boolean.TRUE.equals(res)){
            return null;
        }
        String password = passwordEncoder.encode(registerDTO.getPassword());
        String insertSql = "INSERT INTO users (phone,password,nickname) VALUES (?,?,?) ";
        if(registerDTO.getNickname() == null || registerDTO.getNickname().equals("")){
            log.info("111");
            registerDTO.setNickname("妮露"+ Long.toString(System.currentTimeMillis()));
        }
        int j = jdbc.update(insertSql, registerDTO.getPhone(), password, registerDTO.getNickname());
        log.info(String.valueOf(j));

        UserVO userVO = new UserVO();
        String querySql = "SELECT nickname,id,phone FROM users WHERE phone = ?";
        LoginVO loginVO = jdbc.queryForObject(querySql,
                (rs,i)->
                {
                    LoginVO lVO = new LoginVO();
                    lVO.setNickname(rs.getString("nickname"));
                    lVO.setId(rs.getLong("id"));
                    lVO.setPhone(rs.getString("phone"));
                    return lVO;
                },registerDTO.getPhone());
        userVO.setToken(registerDTO.getToken());
        userVO.setLogin(loginVO);
        return userVO;
    }


    /*
    *用户登录
    *
     */
    public UserVO login(LoginDTO loginDTO) {
        String sql = "SELECT id,nickname FROM users WHERE phone = ?";
        String existSql = "SELECT EXISTS(" +
                " SELECT id FROM users WHERE phone = ?)";
        Boolean res = jdbc.queryForObject(existSql, Boolean.class,loginDTO.getPhone());
        UserVO userVO = new UserVO();
        if ( !Boolean.TRUE.equals(res) ) {
            return null;
        }

        LoginVO loginVO = jdbc.queryForObject(sql,
                (rs,i)-> {
                    LoginVO lv = new LoginVO();
                    lv.setId(rs.getLong("id"));
                    lv.setNickname(rs.getString("nickname"));
                    return lv;
                }
                ,loginDTO.getPhone());
        userVO.setToken(loginDTO.getToken());
        String token = jwtTokenService.generateToken(loginVO.getId());
        userVO.setLogin(loginVO);
        userVO.setToken(token);
        return userVO;
    }
}
