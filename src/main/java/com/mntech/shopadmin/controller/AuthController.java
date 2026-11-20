package com.mntech.shopadmin.controller;

import com.mntech.shopadmin.dto.LoginRequest;
import com.mntech.shopadmin.dto.RegisterRequest;
import com.mntech.shopadmin.dto.response.ApiResponse;
import com.mntech.shopadmin.dto.response.AuthResponse;
import com.mntech.shopadmin.entity.SysUser;
import com.mntech.shopadmin.service.IJwtService;
import com.mntech.shopadmin.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IUserService userService;
    private final IJwtService jwtService;

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        SysUser user = userService.register(request);
        String token = jwtService.generateToken(user.getId());

        AuthResponse response = new AuthResponse(
                token,
                new AuthResponse.UserInfo(user.getId(), user.getUsername(), user.getEmail(), user.getRole())
        );

        return ApiResponse.success("注册成功", response);
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        SysUser user = userService.login(request);
        String token = jwtService.generateToken(user.getId());

        AuthResponse response = new AuthResponse(
                token,
                new AuthResponse.UserInfo(user.getId(), user.getUsername(), user.getEmail(), user.getRole())
        );

        return ApiResponse.success("登录成功", response);
    }

    @GetMapping("/me")
    public ApiResponse<SysUser> getCurrentUser(@RequestAttribute("userId") Long userId) {
        SysUser user = userService.getCurrentUser(userId);
        return ApiResponse.success("获取用户信息成功", user);
    }
}
