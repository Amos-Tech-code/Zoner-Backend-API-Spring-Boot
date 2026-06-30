package com.amos_tech_code.zoner.auth.repository;

import com.amos_tech_code.zoner.auth.entity.EmailVerification;
import com.amos_tech_code.zoner.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, UUID> {

    Optional<EmailVerification> findTopByUserIdAndVerifiedAtIsNullOrderByCreatedAtDesc(
            UUID userId
    );

    void deleteByExpiresAtBefore(Instant instant);

}
