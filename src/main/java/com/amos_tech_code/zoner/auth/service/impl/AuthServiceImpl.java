package com.amos_tech_code.zoner.auth.service.impl;

import com.amos_tech_code.zoner.auth.dto.request.*;
import com.amos_tech_code.zoner.auth.dto.response.CompleteProfileResponse;
import com.amos_tech_code.zoner.auth.dto.response.VerifyEmailResponse;
import com.amos_tech_code.zoner.auth.event.EmailVerifiedEvent;
import com.amos_tech_code.zoner.common.exception.InvalidVerificationCodeException;
import com.amos_tech_code.zoner.common.exception.ResourceNotFoundException;
import com.amos_tech_code.zoner.config.properties.AuthProperties;
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
import com.amos_tech_code.zoner.users.service.UsernameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import com.amos_tech_code.zoner.auth.service.AuthService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

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

    private final Clock clock;

    private final UsernameService usernameService;

    @Override
    public RegisterResponse register(RegisterRequest request) {

        log.info("Registering new user with email {}", request.email());

        Optional<User> existingUser =
                userRepository.findByEmailAndDeletedAtIsNull(request.email());

        if (existingUser.isPresent()) {

            User user = existingUser.get();

            if (user.isEmailVerified()) {
                throw new DuplicateResourceException(
                        "An account with this email already exists."
                );
            }

            return resendVerificationForExistingUser(user);
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
                                Instant.now(clock).plus(
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

    private RegisterResponse resendVerificationForExistingUser(User user) {

        log.info(
                "Resending verification email to existing unverified user {}",
                user.getEmail()
        );

        // Find the latest one
        EmailVerification verification =
                emailVerificationRepository
                        .findTopByUserIdAndVerifiedAtIsNullOrderByCreatedAtDesc(
                                user.getId()
                        )
                        .orElseThrow();

        // Generate a new code
        String verificationCode = otpService.generateOtp();

        verification.setVerificationCodeHash(
                passwordService.hash(verificationCode)
        );

        verification.setExpiresAt(
                Instant.now(clock)
                        .plus(authProperties.getVerificationCodeExpiry())
        );

        verification.setAttempts(0);

        verification.setVerifiedAt(null);

        emailVerificationRepository.save(verification);

        eventPublisher.publishEvent(
                new UserRegisteredEvent(
                        user.getId(),
                        user.getEmail(),
                        verificationCode
                )
        );

        // Return response
        return AuthMapper.toRegisterResponse(
                user,
                "A new verification code has been sent to your email."
        );
    }

    @Override
    public VerifyEmailResponse verifyEmail(VerifyEmailRequest request) {

        User user = userRepository
                .findByEmailAndDeletedAtIsNull(request.email())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found.")
                );

        if(user.isEmailVerified()) {
            log.info("Email {} already verified.", user.getEmail());

            return AuthMapper.toVerifyEmailResponse(
                    user,
                    "Email is already verified."
            );
        }

        EmailVerification verification =
                emailVerificationRepository
                        .findTopByUserIdAndVerifiedAtIsNullOrderByCreatedAtDesc(user.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("No active verification found.")
                        );

        if (verification.getExpiresAt().isBefore(Instant.now(clock))) {

            throw new InvalidVerificationCodeException(
                    "Verification code has expired."
            );

        }

        if (verification.getAttempts() >= authProperties.getMaxVerificationAttempts()) {

            throw new InvalidVerificationCodeException(
                    "Maximum verification attempts exceeded."
            );

        }

        boolean matches =
                passwordService.matches(request.code(), verification.getVerificationCodeHash());

        if (!matches) {
            saveVerificationAttempts(verification); // Call the new method to save attempts

            throw new InvalidVerificationCodeException(
                    "Invalid verification code."
            );
        }

        verification.setVerifiedAt(Instant.now(clock));

        emailVerificationRepository.save(verification);

        user.setEmailVerified(true);

        user.setRegistrationStage(RegistrationStage.EMAIL_VERIFIED);

        user.setAccountStatus(AccountStatus.ACTIVE);

        userRepository.save(user);

        eventPublisher.publishEvent(
                new EmailVerifiedEvent(
                        user.getId(),
                        user.getEmail()
                )
        );

        return AuthMapper.toVerifyEmailResponse(
                user,
                "Email verified successfully."
        );

    }

    // New method to increment and save verification attempts in a new transaction
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveVerificationAttempts(EmailVerification verification) {
        verification.setAttempts(verification.getAttempts() + 1);
        emailVerificationRepository.save(verification);
    }

    @Override
    public void resendVerificationCode(ResendVerificationRequest request) {

    }

    @Override
    public CompleteProfileResponse completeProfile(
            CompleteProfileRequest request
    ) {

        log.info("Completing profile for {}", request.email());

        User user = userRepository
                .findByEmailAndDeletedAtIsNull(request.email())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found.")
                );

        if (!user.isEmailVerified()) {
            throw new IllegalStateException(
                    "Email must be verified before completing profile."
            );
        }

        if (user.getRegistrationStage() ==
                RegistrationStage.PROFILE_COMPLETED
                || user.getRegistrationStage() ==
                RegistrationStage.BUSINESS_ADDED) {

            return AuthMapper.toCompleteProfileResponse(
                    user,
                    "Profile has already been completed."
            );
        }

        if (!usernameService.isAvailable(request.username())) {

            return AuthMapper.usernameUnavailable(
                    user,
                    request.username(),
                    usernameService.generateSuggestions(
                            request.username()
                    )
            );
        }

        user.setDisplayName(request.displayName());

        user.setUsername(
                request.username()
                        .trim()
                        .toLowerCase()
        );

        user.setRegistrationStage(
                RegistrationStage.PROFILE_COMPLETED
        );

        userRepository.save(user);

        log.info("Profile completed successfully for user {}", user.getId()
        );

        return AuthMapper.toCompleteProfileResponse(
                user,
                "Profile completed successfully."
        );
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        return null;
    }

}