package com.amos_tech_code.zoner.auth.service;

import io.jsonwebtoken.Claims;

import java.util.Map;
import java.util.UUID;

public interface JwtService {

    String generateAccessToken(
            UUID userId,
            String email,
            Map<String, Object> claims
    );

    String generateRefreshToken(
            UUID userId,
            UUID refreshTokenId
    );

    Claims getClaims(String token);

    UUID extractUserId(String token);

    UUID extractSessionId(String token);

    boolean isTokenValid(String token);

    boolean isExpired(String token);

}
