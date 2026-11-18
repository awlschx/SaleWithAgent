package com.chx.saleswithagent.Security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@Data
public class JwtTokenService {
    /*注入token生成逻辑和有效时间*/
    /*构造方法注入*/

    private final SecretKey secret;
    private final long ttl;
    public JwtTokenService(@Value("${app.jwt.secret}") String secret,
                           @Value("${app.jwt.ttl-seconds}") long ttl) {
        this.secret =Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.ttl = ttl;
    }

    /*
    *生成token
     */
    public String generateToken(Long userId) {
        return Jwts.builder()//入口
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
}
