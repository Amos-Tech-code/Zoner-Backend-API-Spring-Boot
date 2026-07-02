package com.amos_tech_code.zoner.auth.repository;

import com.amos_tech_code.zoner.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findById(UUID id);

    Optional<RefreshToken> findByUserIdAndDeviceIdAndRevokedAtIsNull(
            UUID userId,
            String deviceId
    );

}
