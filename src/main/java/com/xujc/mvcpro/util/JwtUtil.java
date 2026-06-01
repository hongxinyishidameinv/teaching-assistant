package com.xujc.mvcpro.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT工具类
 * 用于生成、验证和解析JWT Token
 */
@Component
public class JwtUtil {

    // JWT密钥（实际项目中应该从配置文件读取）
    private static final String SECRET_KEY = "xujc-mvc-pro-jwt-secret-key-2024-very-long-and-secure";
    
    // Token有效期：24小时（毫秒）
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    // 生成密钥
    private SecretKey getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成JWT Token
     *
     * @param uid       用户ID
     * @param username  用户名
     * @param type      用户类型
     * @return JWT Token字符串
     */
    public String generateToken(Integer uid, String username, Integer type) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .subject(String.valueOf(uid))
                .claim("username", username)
                .claim("type", type)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 从Token中获取用户ID
     *
     * @param token JWT Token
     * @return 用户ID
     */
    public Integer getUidFromToken(String token) {
        Claims claims = parseToken(token);
        return Integer.parseInt(claims.getSubject());
    }

    /**
     * 从Token中获取用户名
     *
     * @param token JWT Token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }

    /**
     * 从Token中获取用户类型
     *
     * @param token JWT Token
     * @return 用户类型
     */
    public Integer getTypeFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("type", Integer.class);
    }

    /**
     * 验证Token是否有效
     *
     * @param token JWT Token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            // 检查是否过期
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 解析Token
     *
     * @param token JWT Token
     * @return Claims对象
     */
    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 获取Token的过期时间
     *
     * @return 过期时间（毫秒）
     */
    public long getExpirationTime() {
        return EXPIRATION_TIME;
    }
}
