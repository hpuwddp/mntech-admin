package com.mntech.shopadmin.service.impl;

import com.mntech.shopadmin.service.IJwtService;
import com.mntech.shopadmin.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService implements IJwtService {
    private final JwtUtil jwtUtil;

    @Override
    public String generateToken(Long userId) {
        return jwtUtil.generateToken(userId);
    }
    @Override
    public Long extractUserId(String token) {
        return jwtUtil.extractUserId(token);
    }
    @Override
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }
}
