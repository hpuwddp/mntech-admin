package com.mntech.shopadmin.service;

public interface IJwtService {
    public String generateToken(Long userId);

    public Long extractUserId(String token);

    public boolean validateToken(String token);
}
