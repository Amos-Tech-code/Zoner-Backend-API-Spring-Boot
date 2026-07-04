package com.amos_tech_code.zoner.auth.service.impl;

import com.amos_tech_code.zoner.auth.dto.internal.DeviceInfo;
import com.amos_tech_code.zoner.auth.dto.internal.RefreshTokenResult;
import com.amos_tech_code.zoner.auth.dto.request.LoginRequest;
import com.amos_tech_code.zoner.auth.entity.RefreshToken;
import com.amos_tech_code.zoner.auth.repository.RefreshTokenRepository;
import com.amos_tech_code.zoner.auth.service.JwtService;
import com.amos_tech_code.zoner.auth.service.RefreshTokenService;
import com.amos_tech_code.zoner.common.exception.InvalidTokenException;
import com.amos_tech_code.zoner.config.properties.JwtProperties;
import com.amos_tech_code.zoner.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository repository;

    private final JwtService jwtService;

    private final JwtProperties jwtProperties;

    private final Clock clock;

    @Override
    public RefreshTokenResult create(
        User user,
        DeviceInfo deviceInfo
    ) {

        UUID tokenId = UUID.randomUUID();

        String rawRefreshToken =
        jwtService.generateRefreshToken(
            user.getId(),
            tokenId
        );

        // Use SHA-256 for hashing refresh tokens
        String hashedToken = hashTokenWithSHA256(rawRefreshToken);

        RefreshToken refreshToken =
        RefreshToken.builder()
            .id(tokenId)
            .user(user)
            .tokenHash(hashedToken)
            .deviceId(deviceInfo.deviceId())
            .deviceName(deviceInfo.deviceName())
            .platform(deviceInfo.platform())
            .userAgent(deviceInfo.userAgent())
            .ipAddress(deviceInfo.ipAddress())
            .expiresAt(
                Instant.now(clock)
                    .plus(jwtProperties.getRefreshTokenExpiration())
            ).createdAt(Instant.now(clock))
            .build();

        repository.save(refreshToken);

        return new RefreshTokenResult(
                refreshToken,
        rawRefreshToken
        );

    }

    @Override
    public void revokeActiveSession(UUID userId, String deviceId) {
        repository
                .findByUserIdAndDeviceIdAndRevokedAtIsNull(
                        userId,
                        deviceId
                )
                .ifPresent(this::revoke);
    }

    @Override
    public void revoke(RefreshToken refreshToken) {

        if (refreshToken.getRevokedAt() != null) {
            return;
        }

        refreshToken.setRevokedAt(Instant.now(clock));

        repository.save(refreshToken);

        repository.flush();
    }

    @Override
    public void revokeAll(UUID userId) {

        repository.revokeAllActiveByUserId(
                userId,
                Instant.now(clock)
        );

    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken findActive(UUID sessionId) {

        RefreshToken token =
                repository.findById(sessionId)
                        .orElseThrow(() ->
                                new InvalidTokenException(
                                        "Invalid refresh token."
                                ));

        if (token.getRevokedAt() != null) {
            throw new InvalidTokenException(
                    "Refresh token has been revoked."
            );
        }

        if (token.getExpiresAt().isBefore(Instant.now(clock))) {
            throw new InvalidTokenException(
                    "Refresh token has expired."
            );
        }

        return token;
    }

    @Override
    public void updateLastUsed(
            RefreshToken refreshToken
    ) {

        refreshToken.setLastUsedAt(Instant.now(clock));

        repository.save(refreshToken);

    }

    @Override
    public boolean matches(
            RefreshToken refreshToken,
            String rawToken
    ) {

        return refreshToken
                .getTokenHash()
                .equals(hashTokenWithSHA256(rawToken));

    }

    @Override
    public RefreshTokenResult rotate(
            RefreshToken currentToken,
            User user
    ) {
        currentToken.setLastUsedAt(Instant.now(clock));

        revoke(currentToken);

        DeviceInfo deviceInfo =
                new DeviceInfo(
                        currentToken.getDeviceId(),
                        currentToken.getDeviceName(),
                        currentToken.getPlatform(),
                        currentToken.getUserAgent(),
                        currentToken.getIpAddress()
                );

        return create(
                user,
                deviceInfo
        );

    }

    @Override
    @Transactional(readOnly = true)
    public List<RefreshToken> findActiveSessions(UUID userId) {

        return repository
                .findAllByUserIdAndRevokedAtIsNullOrderByLastUsedAtDesc(
                        userId
                );

    }

    // Helper method to hash tokens using SHA-256
    private static String hashTokenWithSHA256(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes());
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not found", e);
        }
    }
}