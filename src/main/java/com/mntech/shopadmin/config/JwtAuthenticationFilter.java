package com.mntech.shopadmin.config;

import com.mntech.shopadmin.entity.SysUser;
import com.mntech.shopadmin.service.IJwtService;
import com.mntech.shopadmin.service.IUserService;
import com.mntech.shopadmin.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final IUserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final Long userId;

        System.out.println("请求路径: " + request.getRequestURI());
        System.out.println("Authorization头: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("没有Authorization头或格式不正确");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        System.out.println("JWT Token: " + jwt);

        try {
            userId = jwtUtil.extractUserId(jwt);
            System.out.println("从Token提取的UserId: " + userId);

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                SysUser user = userService.getCurrentUser(userId);
                System.out.println("找到用户: " + user.getUsername());

                if (jwtUtil.validateToken(jwt)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("认证成功");
                } else {
                    System.out.println("Token验证失败");
                }
            }
        } catch (Exception e) {
            System.out.println("JWT验证异常: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}