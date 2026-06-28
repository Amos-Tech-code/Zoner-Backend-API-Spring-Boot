package com.amos_tech_code.zoner.auth.repository;

import com.amos_tech_code.zoner.auth.entity.EmailVerification;
import com.amos_tech_code.zoner.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, UUID> {

    Optional<EmailVerification> findTopByUserOrderByCreatedAtDesc(User user);

    Optional<EmailVerification> findTopByUserAndVerifiedAtIsNullOrderByCreatedAtDesc(
            User user
    );

    void deleteByExpiresAtBefore(Instant instant);

}
