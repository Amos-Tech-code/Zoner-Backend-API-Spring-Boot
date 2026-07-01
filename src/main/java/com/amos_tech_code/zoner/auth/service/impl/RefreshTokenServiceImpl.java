package com.amos_tech_code.zoner.auth.service.impl;

import com.amos_tech_code.zoner.auth.dto.internal.RefreshTokenResult;
import com.amos_tech_code.zoner.auth.dto.request.LoginRequest;
import com.amos_tech_code.zoner.auth.entity.RefreshToken;
import com.amos_tech_code.zoner.auth.repository.RefreshTokenRepository;
import com.amos_tech_code.zoner.auth.service.JwtService;
import com.amos_tech_code.zoner.auth.service.PasswordService;
import com.amos_tech_code.zoner.auth.service.RefreshTokenService;
import com.amos_tech_code.zoner.config.properties.JwtProperties;
import com.amos_tech_code.zoner.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository repository;

    private final JwtService jwtService;

    private final PasswordService passwordService;

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

        String hashedToken =
        passwordService.hash(rawRefreshToken);

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

}