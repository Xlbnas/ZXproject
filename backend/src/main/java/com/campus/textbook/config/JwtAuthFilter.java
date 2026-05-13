package com.campus.textbook.config;

import com.campus.textbook.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT认证过滤器，每次请求都会执行
 * 从请求头中取出token并验证，设置到SecurityContext中
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 从请求头获取token
        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            // 也支持直接放token在header里（兼容前端两种写法）
            token = request.getHeader("token");
        }

        if (StringUtils.hasText(token) && jwtUtils.isValid(token)) {
            Claims claims = jwtUtils.parseToken(token);
            Integer userId = Integer.valueOf(claims.getSubject());

            // 根据用户类型设置权限（注意：JJWT 反序列化数字可能是 Integer/Long，统一用 Number 处理）
            Object userTypeObj = claims.get("userType");
            int userType = 1;
            if (userTypeObj instanceof Number) {
                userType = ((Number) userTypeObj).intValue();
            }
            String role = (userType == 2) ? "ROLE_ADMIN" : "ROLE_USER";

            var auth = new UsernamePasswordAuthenticationToken(
                    userId, null, List.of(new SimpleGrantedAuthority(role)));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }
}
