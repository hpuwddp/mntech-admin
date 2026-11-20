package com.mntech.shopadmin.service;


import com.mntech.shopadmin.dto.LoginRequest;
import com.mntech.shopadmin.dto.RegisterRequest;
import com.mntech.shopadmin.entity.SysUser;

public interface IUserService {
    SysUser register(RegisterRequest request);
    SysUser login(LoginRequest request);
    SysUser getCurrentUser(Long userId);
    SysUser findByUsername(String username);
}