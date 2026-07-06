package com.amos_tech_code.zoner.auth.repository;

import com.amos_tech_code.zoner.auth.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, UUID> {

    Optional<PasswordResetToken>
    findTopByUserIdAndVerifiedAtIsNullOrderByIdDesc(UUID userId);

}
