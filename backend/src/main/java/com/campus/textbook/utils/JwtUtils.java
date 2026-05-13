package com.campus.textbook.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT工具类，负责生成和解析Token
 */
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expire}")
    private long expire;

    /** 生成JWT Token */
    public String generateToken(Integer userId, String username, Integer userType) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("userType", userType)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expire))
                .signWith(key)
                .compact();
    }

    /** 解析Token，获取Claims */
    public Claims parseToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            return null;
        }
    }

    /** 从Token中获取用户ID */
    public Integer getUserId(String token) {
        Claims claims = parseToken(token);
        if (claims == null) return null;
        return Integer.valueOf(claims.getSubject());
    }

    /** 从Token中获取用户类型 */
    public Integer getUserType(String token) {
        Claims claims = parseToken(token);
        if (claims == null) return null;
        return (Integer) claims.get("userType");
    }

    /** 判断Token是否有效 */
    public boolean isValid(String token) {
        return parseToken(token) != null;
    }
}
