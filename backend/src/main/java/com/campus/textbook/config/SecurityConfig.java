package com.campus.textbook.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * Spring Security配置
 * 采用无状态JWT认证，关闭Session
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 关闭CSRF（前后端分离不需要）
            .csrf(AbstractHttpConfigurer::disable)
            // 无状态，不使用Session
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 跨域
            .cors(cors -> {})
            // 权限配置
            .authorizeHttpRequests(auth -> auth
                // 公开接口：注册、登录、教材列表、首页公告、环保统计
                .requestMatchers(
                    "/api/user/register",
                    "/api/user/login",
                    "/api/textbook/list",
                    "/api/textbook/detail/**",
                    "/api/announcement/list",
                    "/api/eco/global",
                    "/uploads/**"
                ).permitAll()
                // 管理员接口
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // 其余接口需要登录
                .anyRequest().authenticated()
            )
            // 未认证时的处理（注意：Map.of 不允许 null 值，用 HashMap 替代）
            .exceptionHandling(e -> e
                .authenticationEntryPoint((req, res, ex) -> {
                    res.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
                    res.setStatus(401);
                    java.util.Map<String, Object> body = new java.util.HashMap<>();
                    body.put("code", 401);
                    body.put("msg", "请先登录");
                    body.put("data", null);
                    res.getWriter().write(new ObjectMapper().writeValueAsString(body));
                })
                .accessDeniedHandler((req, res, ex) -> {
                    res.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
                    res.setStatus(403);
                    java.util.Map<String, Object> body = new java.util.HashMap<>();
                    body.put("code", 403);
                    body.put("msg", "无权限访问");
                    body.put("data", null);
                    res.getWriter().write(new ObjectMapper().writeValueAsString(body));
                })
            )
            // 加入JWT过滤器
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
