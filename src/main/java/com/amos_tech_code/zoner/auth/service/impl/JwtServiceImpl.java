package com.amos_tech_code.zoner.auth.service.impl;

import com.amos_tech_code.zoner.auth.service.JwtService;
import com.amos_tech_code.zoner.config.properties.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.lang.Collections;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private static final String EMAIL_CLAIM = "email";

    private final JwtProperties jwtProperties;
    private final Clock clock;

    private SecretKey secretKey;

    @PostConstruct
    void init() {
        this.secretKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecret()
                        .getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    public String generateAccessToken(
            UUID userId,
            UUID sessionId,
            String email,
            Map<String, Object> claims
    ) {

        Map<String, Object> allClaims = new HashMap<>(claims);
        allClaims.put(EMAIL_CLAIM, email);

        return buildToken(
                userId,
                sessionId,
                allClaims,
                jwtProperties.getAccessTokenExpiration()
        );
    }

    @Override
    public String generateRefreshToken(
            UUID userId,
            UUID refreshTokenId
    ) {

        return buildToken(
                userId,
                refreshTokenId,
                Collections.emptyMap(),
                jwtProperties.getRefreshTokenExpiration()
        );
    }

    @Override
    public Claims getClaims(String token) {
        return parse(token).getPayload();
    }

    @Override
    public UUID extractUserId(String token) {
        return UUID.fromString(
                getClaims(token).getSubject()
        );
    }

    @Override
    public UUID extractSessionId(String token) {
        return UUID.fromString(
                getClaims(token).getId()
        );
    }

    @Override
    public boolean isTokenValid(String token) {

        try {

            parse(token);

            return true;

        } catch (JwtException | IllegalArgumentException ex) {

            return false;

        }
    }

    @Override
    public boolean isExpired(String token) {

        try {

            return getClaims(token)
                    .getExpiration()
                    .before(Date.from(Instant.now(clock)));

        } catch (ExpiredJwtException ex) {

            return true;

        }
    }

    private String buildToken(
            UUID userId,
            UUID jwtId,
            Map<String, Object> claims,
            Duration expiration
    ) {

        Instant now = Instant.now(clock);

        JwtBuilder builder = Jwts.builder()
                .subject(userId.toString())
                .issuer(jwtProperties.getIssuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration)))
                .claims(claims)
                .signWith(secretKey);

        if (jwtId != null) {
            builder.id(jwtId.toString());
        }

        return builder.compact();
    }

    private Jws<Claims> parse(String token) {

        return Jwts.parser()
                .requireIssuer(jwtProperties.getIssuer())
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
    }
}