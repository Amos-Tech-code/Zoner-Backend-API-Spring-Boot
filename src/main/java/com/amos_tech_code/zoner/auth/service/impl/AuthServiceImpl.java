package com.amos_tech_code.zoner.auth.service.impl;

import com.amos_tech_code.zoner.config.properties.AuthProperties;
import com.amos_tech_code.zoner.auth.dto.request.LoginRequest;
import com.amos_tech_code.zoner.auth.dto.request.RegisterRequest;
import com.amos_tech_code.zoner.auth.dto.request.ResendVerificationRequest;
import com.amos_tech_code.zoner.auth.dto.request.VerifyEmailRequest;
import com.amos_tech_code.zoner.auth.dto.response.LoginResponse;
import com.amos_tech_code.zoner.auth.dto.response.RegisterResponse;
import com.amos_tech_code.zoner.auth.entity.AuthAccount;
import com.amos_tech_code.zoner.auth.entity.EmailVerification;
import com.amos_tech_code.zoner.auth.event.UserRegisteredEvent;
import com.amos_tech_code.zoner.auth.mapper.AuthMapper;
import com.amos_tech_code.zoner.auth.repository.AuthAccountRepository;
import com.amos_tech_code.zoner.auth.repository.EmailVerificationRepository;
import com.amos_tech_code.zoner.auth.service.OtpService;
import com.amos_tech_code.zoner.auth.service.PasswordService;
import com.amos_tech_code.zoner.common.enums.Visibility;
import com.amos_tech_code.zoner.common.exception.DuplicateResourceException;
import com.amos_tech_code.zoner.users.entity.User;
import com.amos_tech_code.zoner.users.enums.AccountStatus;
import com.amos_tech_code.zoner.users.enums.AuthProvider;
import com.amos_tech_code.zoner.users.enums.RegistrationStage;
import com.amos_tech_code.zoner.users.enums.Role;
import com.amos_tech_code.zoner.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import com.amos_tech_code.zoner.auth.service.AuthService;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final AuthAccountRepository authAccountRepository;

    private final EmailVerificationRepository emailVerificationRepository;

    private final PasswordService passwordService;

    private final OtpService otpService;

    private final ApplicationEventPublisher eventPublisher;

    private final AuthProperties authProperties;

    @Override
    public RegisterResponse register(RegisterRequest request) {

        log.info("Registering new user with email {}", request.email());

        if(userRepository.existsByEmailAndDeletedAtIsNull(request.email())) {
            throw new DuplicateResourceException(
                    "An account with this email already exists."
            );
        }

        // Create user
        User user = User.builder()
                .email(request.email())
                .passwordHash(
                        passwordService.hash(request.password())
                )
                .role(Role.USER)
                .registrationStage(
                        RegistrationStage.EMAIL_SUBMITTED
                )
                .accountStatus(AccountStatus.PENDING)
                .emailVerified(false)
                .notificationsEnabled(true)
                .twoFactorEnabled(false)
                .visibility(Visibility.PUBLIC)
                .build();

        user = userRepository.save(user);

        // Create auth account
        AuthAccount authAccount =
                AuthAccount.builder()
                        .user(user)
                        .provider(AuthProvider.EMAIL)
                        .providerUserId(user.getEmail())
                        .email(user.getEmail())
                        .build();

        authAccountRepository.save(authAccount);

        log.info("User {} created successfully", user.getId());

        // Generate OTP
        String otp = otpService.generateOtp();

        String otpHash = passwordService.hash(otp);

        // Create email verification
        EmailVerification verification =
                EmailVerification.builder()
                        .user(user)
                        .verificationCodeHash(otpHash)
                        .expiresAt(
                                Instant.now().plus(
                                        authProperties.getVerificationCodeExpiry()
                                )
                        )
                        .attempts(0)
                        .verifiedAt(null)
                        .build();

        emailVerificationRepository.save(
                verification
        );

        // Publish event for sending verification email
        eventPublisher.publishEvent(
                new UserRegisteredEvent(
                        user.getId(),
                        user.getEmail(),
                        otp
                )
        );

        // Return response
        return AuthMapper.toRegisterResponse(
                user,
                "A verification code has been sent to your email."
        );

    }

    @Override
    public void verifyEmail(VerifyEmailRequest request) {

    }

    @Override
    public void resendVerificationCode(ResendVerificationRequest request) {

    }

    @Override
    public LoginResponse login(LoginRequest request) {
        return null;
    }

}
