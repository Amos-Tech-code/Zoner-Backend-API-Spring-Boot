package com.amos_tech_code.zoner.auth.service.impl;

import com.amos_tech_code.zoner.users.entity.User;
import org.springframework.stereotype.Service;
import com.amos_tech_code.zoner.auth.service.JwtService;

import java.util.UUID;

@Service
public class JwtServiceImpl implements JwtService {

    @Override
    public String generateAccessToken(User user) {
        return "";
    }

    @Override
    public String generateRefreshToken(User user) {
        return "";
    }

    @Override
    public UUID extractUserId(String token) {
        return null;
    }

    @Override
    public boolean isValid(String token) {
        return false;
    }
}
