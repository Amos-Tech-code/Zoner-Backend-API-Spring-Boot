package com.amos_tech_code.zoner.auth.service;

import com.amos_tech_code.zoner.auth.dto.internal.RefreshTokenResult;
import com.amos_tech_code.zoner.auth.entity.RefreshToken;
import com.amos_tech_code.zoner.auth.dto.request.LoginRequest;
import com.amos_tech_code.zoner.users.entity.User;

import java.util.UUID;

public interface RefreshTokenService {

    RefreshTokenResult create(
            User user,
            LoginRequest request,
            String userAgent,
            String ipAddress
    );

    void revokeActiveSession(
            UUID userId,
            String deviceId
    );

    void revoke(RefreshToken refreshToken);

}
