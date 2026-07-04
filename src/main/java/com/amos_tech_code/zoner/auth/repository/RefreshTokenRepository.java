package com.amos_tech_code.zoner.auth.repository;

import com.amos_tech_code.zoner.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findById(UUID id);

    Optional<RefreshToken> findByUserIdAndDeviceIdAndRevokedAtIsNull(
            UUID userId,
            String deviceId
    );

    Optional<RefreshToken> findByIdAndRevokedAtIsNull(UUID id);

    List<RefreshToken> findAllByUserIdAndRevokedAtIsNull(UUID userId);

    Optional<RefreshToken> findByIdAndUserId(UUID id, UUID userId);

    @Modifying
    @Query("""
    UPDATE RefreshToken rt
       SET rt.revokedAt = :revokedAt
     WHERE rt.user.id = :userId
       AND rt.revokedAt IS NULL
    """)
    int revokeAllActiveByUserId(
            UUID userId,
            Instant revokedAt
    );

    List<RefreshToken> findAllByUserIdAndRevokedAtIsNullOrderByLastUsedAtDesc(
            UUID userId
    );

}
