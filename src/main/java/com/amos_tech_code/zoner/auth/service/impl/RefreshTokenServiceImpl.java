package com.amos_tech_code.zoner.auth.service.impl;

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
        LoginRequest request,
        String userAgent,
        String ipAddress
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
            .deviceId(request.deviceId())
            .deviceName(request.deviceName())
            .platform(request.platform())
            .userAgent(userAgent)
            .ipAddress(ipAddress)
            .expiresAt(
                Instant.now(clock)
                    .plus(jwtProperties.getRefreshTokenExpiration())
            )
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

        refreshToken.setRevokedAt(
            Instant.now(clock)
        );

        repository.save(refreshToken);

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