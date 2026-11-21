package com.chx.saleswithagent.Interceptor;


import ch.qos.logback.classic.Logger;
import com.chx.saleswithagent.Common.UserContext;
import com.chx.saleswithagent.Result.Result;
import com.chx.saleswithagent.Security.JwtTokenService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@Data
@Slf4j
public class JwtAuthenticationInterceptor implements HandlerInterceptor {
    private final JwtTokenService jwtTokenService;
    private final ObjectMapper objectMapper;


    public JwtAuthenticationInterceptor(JwtTokenService jwtTokenService, ObjectMapper objectMapper) {
        this.jwtTokenService = jwtTokenService;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        //从请求头获取auth
        String authorization = request.getHeader("Authorization");
        //判断
        if(authorization == null || !authorization.startsWith("Bearer ")){
            //非法，返回错误信息
           writeErrorResponse(response,"非法信息");
           return false;

        }
        if("OPTIONS".equals(request.getMethod())){
            return  true;
        }
        authorization = authorization.substring("Bearer ".length()).trim();
        Claims claims = jwtTokenService.parseClaims(authorization);
        String jti = claims.getId();
        if(jwtTokenService.isBlacklist(jti)){
            return false;
        }


        //解析token
        try{
            //校验
            Long userId = Long.valueOf(claims.getSubject());
            UserContext.setUserId(userId);
            log.info("userId{}",userId);
            return true;
        }catch (Exception e){
            writeErrorResponse(response,e.getMessage());
        }
        return false;
    }
    /*
     *写错误信息
     */
    private void writeErrorResponse(@NotNull HttpServletResponse response,String message){
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Result fail = Result.fail(401, message);

        try {
            response.getWriter().write(objectMapper.writeValueAsString(fail));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /*
    *视图渲染之后移除usercontext，防止内存泄露
     */
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,Exception ex) throws Exception {
        UserContext.clear();
    }
}
