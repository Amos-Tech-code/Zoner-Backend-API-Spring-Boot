package com.amos_tech_code.zoner.auth.service.impl;

import com.amos_tech_code.zoner.auth.dto.response.RegisterResponse;
import com.amos_tech_code.zoner.auth.entity.EmailVerification;
import com.amos_tech_code.zoner.auth.event.UserRegisteredEvent;
import com.amos_tech_code.zoner.auth.mapper.AuthMapper;
import com.amos_tech_code.zoner.auth.repository.EmailVerificationRepository;
import com.amos_tech_code.zoner.auth.service.EmailVerificationService;
import com.amos_tech_code.zoner.auth.service.OtpService;
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
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final EmailVerificationRepository repository;

    private final PasswordService passwordService;

    private final OtpService otpService;

    private final AuthProperties authProperties;

    private final Clock clock;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void createVerification(User user) {

        String otp = otpService.generateOtp();

        EmailVerification verification =
                EmailVerification.builder()
                        .user(user)
                        .verificationCodeHash(
                                passwordService.hash(otp)
                        )
                        .expiresAt(
                                Instant.now(clock)
                                        .plus(authProperties.getVerificationCodeExpiry())
                        )
                        .attempts(0)
                        .verifiedAt(null)
                        .build();

        repository.save(verification);

        eventPublisher.publishEvent(
                new UserRegisteredEvent(
                        user.getId(),
                        user.getEmail(),
                        otp
                )
        );
    }

    @Override
    @Transactional
    public RegisterResponse resendVerification(User user) {

        EmailVerification verification = getActiveVerification(user);

        String otp = otpService.generateOtp();

        verification.setVerificationCodeHash(passwordService.hash(otp));

        verification.setExpiresAt(
                Instant.now(clock)
                        .plus(authProperties.getVerificationCodeExpiry())
        );

        verification.setAttempts(0);

        verification.setVerifiedAt(null);

        repository.save(verification);

        eventPublisher.publishEvent(
                new UserRegisteredEvent(
                        user.getId(),
                        user.getEmail(),
                        otp
                )
        );

        return AuthMapper.toRegisterResponse(
                user,
                "A new verification code has been sent to your email."
        );
    }

    @Override
    @Transactional(readOnly = true)
    public EmailVerification getActiveVerification(User user) {

        return repository
                .findTopByUserIdAndVerifiedAtIsNullOrderByCreatedAtDesc(
                        user.getId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException("No active verification found.")
                );
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void incrementAttempts(EmailVerification verification) {

        verification.setAttempts(verification.getAttempts() + 1);

        repository.save(verification);
    }

    @Override
    @Transactional
    public void markVerified(EmailVerification verification) {

        verification.setVerifiedAt(Instant.now(clock));

        repository.save(verification);
    }
}