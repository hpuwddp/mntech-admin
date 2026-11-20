package com.mntech.shopadmin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mntech.shopadmin.dao.UserMapper;
import com.mntech.shopadmin.dto.LoginRequest;
import com.mntech.shopadmin.dto.RegisterRequest;
import com.mntech.shopadmin.entity.SysUser;
import com.mntech.shopadmin.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl  implements IUserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public SysUser register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userMapper.findByUsername(request.getUsername()) != null) {
            throw new RuntimeException("用户名已被使用");
        }
        if (userMapper.findByEmail(request.getEmail()) != null) {
            throw new RuntimeException("邮箱已被注册");
        }

        SysUser user = new SysUser();
        user.setUserName(request.getUsername());
        user.setPassWord(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole("user");

        userMapper.insert(user);
        return user;
    }

    @Override
    public SysUser login(LoginRequest request) {
        SysUser user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        return user;
    }

    @Override
    public SysUser getCurrentUser(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user;
    }

    @Override
    public SysUser findByUsername(String username) {
        return userMapper.findByUsername(username);
    }
}
