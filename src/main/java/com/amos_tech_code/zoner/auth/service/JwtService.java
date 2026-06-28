package com.amos_tech_code.zoner.auth.service;

import com.amos_tech_code.zoner.users.entity.User;

import java.util.UUID;

public interface JwtService {

    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    UUID extractUserId(String token);

    boolean isValid(String token);

}
