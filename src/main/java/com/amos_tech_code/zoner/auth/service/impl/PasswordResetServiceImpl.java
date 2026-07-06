package com.amos_tech_code.zoner.auth.service.impl;

import com.amos_tech_code.zoner.auth.entity.PasswordResetToken;
import com.amos_tech_code.zoner.auth.event.PasswordResetRequestedEvent;
import com.amos_tech_code.zoner.auth.repository.PasswordResetTokenRepository;
import com.amos_tech_code.zoner.auth.service.OtpService;
import com.amos_tech_code.zoner.auth.service.PasswordResetService;
import com.amos_tech_code.zoner.auth.service.PasswordService;
import com.amos_tech_code.zoner.common.exception.ResourceNotFoundException;
import com.amos_tech_code.zoner.config.properties.AuthProperties;
import com.amos_tech_code.zoner.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final PasswordResetTokenRepository repository;

    private final PasswordService passwordService;

    private final OtpService otpService;

    private final AuthProperties authProperties;

    private final Clock clock;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void create(User user) {

        String otp = otpService.generateOtp();

        PasswordResetToken token =
                PasswordResetToken.builder()
                        .user(user)
                        .verificationCodeHash(passwordService.hash(otp))
                        .expiresAt(
                                Instant.now(clock)
                                        .plus(authProperties.getVerificationCodeExpiry())
                        )
                        .attempts(0)
                        .verifiedAt(null)
                        .build();

        repository.save(token);

        eventPublisher.publishEvent(
                new PasswordResetRequestedEvent(
                        user.getId(),
                        user.getEmail(),
                        otp
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PasswordResetToken getActive(User user) {

        return repository
                .findTopByUserIdAndVerifiedAtIsNullOrderByIdDesc(user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No active password reset request found."
                        )
                );

    }

    @Override
    @Transactional
    public void resend(User user) {

        PasswordResetToken token = getActive(user);

        String otp = otpService.generateOtp();

        token.setVerificationCodeHash(
                passwordService.hash(otp)
        );

        token.setExpiresAt(
                Instant.now(clock)
                        .plus(authProperties.getVerificationCodeExpiry())
        );

        token.setAttempts(0);

        token.setVerifiedAt(null);

        repository.save(token);

        eventPublisher.publishEvent(
                new PasswordResetRequestedEvent(
                        user.getId(),
                        user.getEmail(),
                        otp
                )
        );

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void incrementAttempts(PasswordResetToken token) {

        token.setAttempts(token.getAttempts() + 1);

        repository.save(token);

    }

    @Override
    @Transactional
    public void markVerified(PasswordResetToken token) {

        token.setVerifiedAt(
                Instant.now(clock)
        );

        repository.save(token);

    }

}
