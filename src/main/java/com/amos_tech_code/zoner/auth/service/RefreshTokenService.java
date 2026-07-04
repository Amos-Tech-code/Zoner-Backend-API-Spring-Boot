package com.amos_tech_code.zoner.auth.service;

import com.amos_tech_code.zoner.auth.dto.internal.DeviceInfo;
import com.amos_tech_code.zoner.auth.dto.internal.RefreshTokenResult;
import com.amos_tech_code.zoner.auth.entity.RefreshToken;
import com.amos_tech_code.zoner.users.entity.User;

import java.util.List;
import java.util.UUID;

public interface RefreshTokenService {

    RefreshTokenResult create(
            User user,
            DeviceInfo deviceInfo
    );

    void revokeActiveSession(
            UUID userId,
            String deviceId
    );

    void revoke(RefreshToken refreshToken);

    void revokeAll(UUID userId);

    RefreshToken findActive(UUID sessionId);

    void updateLastUsed(RefreshToken refreshToken);

    boolean matches(
            RefreshToken refreshToken,
            String rawToken
    );

    RefreshTokenResult rotate(
            RefreshToken currentToken,
            User user
    );

    List<RefreshToken> findActiveSessions(UUID userId);
}
