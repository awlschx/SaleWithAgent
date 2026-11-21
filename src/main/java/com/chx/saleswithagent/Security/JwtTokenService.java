package com.chx.saleswithagent.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Data
@Slf4j
public class JwtTokenService {
    /*注入token生成逻辑和有效时间*/
    /*构造方法注入*/

    private final SecretKey secret;
    private final long ttl;
    private final StringRedisTemplate redis;
    public JwtTokenService(@Value("${app.jwt.secret}") String secret,
                           @Value("${app.jwt.ttl-seconds}") long ttl,
                           StringRedisTemplate redis) {
        this.secret =Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.ttl = ttl;
        this.redis = redis;
    }

    /*
    *生成token
     */
    public String generateToken(Long userId) {
        return Jwts.builder()//入口
                .id(UUID.randomUUID().toString())
                .subject(String.valueOf(userId))//token的主体
                .issuedAt(new Date())//签发token的时间
                .expiration(new Date(System.currentTimeMillis()+this.ttl))//有效时间
                .signWith(secret)//校验用的密钥
                .compact();

    }

    /*
    *解析返回userId
     */
    public Long parseUserId(String token){
            String userId = Jwts.parser()
                    .verifyWith(secret)//用什么密钥校验
                    .build()
                    .parseSignedClaims(token)//解析token
                    .getPayload()//token分为header，payload，signature
                    .getSubject();//获取生成token时声明的subject
            return Long.parseLong(userId);
    }

    public Claims parseClaims(String token){
        Claims claims = Jwts.parser()
                .verifyWith(secret)            // ← 关键：配置验签密钥
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return  claims;
    }
    /*
    *黑名单设置
     */
    public void blacklist(String token){
        Claims claims = Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        long time = claims.getExpiration().getTime();
        long remaining = time - System.currentTimeMillis();
        if(remaining > 0){
            String id = claims.getId();
            log.info(id);
            redis.opsForValue()
                    .set("jwt:blacklist:"+id,
                            "1",
                            remaining,
                            TimeUnit.MILLISECONDS);
        }
    }

    /*
    *校验当前token是否在黑名单里面
    *
     */
    public boolean isBlacklist(String jti){
        return redis.hasKey("jwt:blacklist:"+jti);
    }
}
