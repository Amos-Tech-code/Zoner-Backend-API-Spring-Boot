package com.amos_tech_code.zoner.auth.service.impl;

import com.amos_tech_code.zoner.auth.dto.internal.DeviceInfo;
import com.amos_tech_code.zoner.auth.dto.internal.GooglePrincipal;
import com.amos_tech_code.zoner.auth.dto.internal.RefreshTokenResult;
import com.amos_tech_code.zoner.auth.dto.request.*;
import com.amos_tech_code.zoner.auth.dto.response.*;
import com.amos_tech_code.zoner.auth.entity.PasswordResetToken;
import com.amos_tech_code.zoner.auth.entity.RefreshToken;
import com.amos_tech_code.zoner.auth.event.AccountStatusChangedEvent;
import com.amos_tech_code.zoner.auth.event.EmailVerifiedEvent;
import com.amos_tech_code.zoner.auth.event.PasswordChangedEvent;
import com.amos_tech_code.zoner.auth.service.*;
import com.amos_tech_code.zoner.business.service.BusinessService;
import com.amos_tech_code.zoner.common.exception.*;
import com.amos_tech_code.zoner.auth.entity.AuthAccount;
import com.amos_tech_code.zoner.auth.entity.EmailVerification;
import com.amos_tech_code.zoner.auth.mapper.AuthMapper;
import com.amos_tech_code.zoner.auth.repository.AuthAccountRepository;
import com.amos_tech_code.zoner.common.enums.Visibility;
import com.amos_tech_code.zoner.config.properties.AuthProperties;
import com.amos_tech_code.zoner.config.properties.JwtProperties;
import com.amos_tech_code.zoner.users.entity.User;
import com.amos_tech_code.zoner.users.enums.AccountStatus;
import com.amos_tech_code.zoner.users.enums.AuthProvider;
import com.amos_tech_code.zoner.users.enums.RegistrationStage;
import com.amos_tech_code.zoner.users.enums.Role;
import com.amos_tech_code.zoner.users.mapper.UserMapper;
import com.amos_tech_code.zoner.users.repository.UserRepository;
import com.amos_tech_code.zoner.users.service.UsernameService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final AuthAccountRepository authAccountRepository;

    private final PasswordService passwordService;

    private final EmailVerificationService emailVerificationService;

    private final UsernameService usernameService;

    private final ApplicationEventPublisher eventPublisher;

    private final Clock clock;

    private final JwtService jwtService;

    private final JwtProperties jwtProperties;

    private final AuthProperties authProperties;

    private final RefreshTokenService refreshTokenService;

    private final PasswordResetService passwordResetService;

    private final BusinessService businessService;
    
    private final GoogleService googleService;

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

            emailVerificationService.resendVerification(user);

            return AuthMapper.toRegisterResponse(
                    user,
                    "A new verification code has been sent to your email."
            );
        }

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
                .visibility(Visibility.PUBLIC)
                .emailVerified(false)
                .notificationsEnabled(true)
                .twoFactorEnabled(false)
                .build();

        user = userRepository.save(user);

        AuthAccount authAccount =
                AuthAccount.builder()
                        .user(user)
                        .provider(AuthProvider.EMAIL)
                        .providerUserId(user.getEmail())
                        .email(user.getEmail())
                        .build();

        authAccountRepository.save(authAccount);

        emailVerificationService.createVerification(user);

        log.info("User {} registered successfully.", user.getId());

        return AuthMapper.toRegisterResponse(
                user,
                "A verification code has been sent to your email."
        );
    }

    @Override
    public VerifyEmailResponse verifyEmail(VerifyEmailRequest request) {

        User user = userRepository
                .findByEmailAndDeletedAtIsNull(request.email())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found."
                        ));

        if (user.isEmailVerified()) {

            return AuthMapper.toVerifyEmailResponse(
                    user,
                    "Email is already verified."
            );
        }

        EmailVerification verification =
                emailVerificationService.getActiveVerification(user);

        if (verification.getExpiresAt().isBefore(Instant.now(clock))) {

            throw new InvalidVerificationCodeException(
                    "Verification code has expired."
            );
        }

        if (verification.getAttempts() >= 5) {

            throw new InvalidVerificationCodeException(
                    "Maximum verification attempts exceeded."
            );
        }

        boolean matches = passwordService.matches(
                request.code(),
                verification.getVerificationCodeHash()
        );

        if (!matches) {

            emailVerificationService.incrementAttempts(
                    verification
            );

            throw new InvalidVerificationCodeException(
                    "Invalid verification code."
            );
        }

        emailVerificationService.markVerified(verification);

        user.setEmailVerified(true);
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setRegistrationStage(
                RegistrationStage.EMAIL_VERIFIED
        );

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

    @Override
    public RegisterResponse resendVerificationCode(
            ResendVerificationRequest request
    ) {

        User user = userRepository
                .findByEmailAndDeletedAtIsNull(request.email())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found."
                        ));

        if (user.isEmailVerified()) {
            throw new DuplicateResourceException(
                    "Email has already been verified."
            );
        }

        return emailVerificationService.resendVerification(user);
    }

    @Override
    public CompleteProfileResponse completeProfile(
            CompleteProfileRequest request
    ) {

        log.info("Completing profile for {}", request.email());

        User user = userRepository
                .findByEmailAndDeletedAtIsNull(request.email())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found."
                        ));

        if (!user.isEmailVerified()) {
            throw new IllegalStateException(
                    "Email must be verified before completing profile."
            );
        }

        if (user.getRegistrationStage()
                == RegistrationStage.PROFILE_COMPLETED
                || user.getRegistrationStage()
                == RegistrationStage.BUSINESS_ADDED) {

            return AuthMapper.toCompleteProfileResponse(
                    user,
                    "Profile has already been completed."
            );
        }

        if (!usernameService.isAvailable(
                request.username()
        )) {

            return AuthMapper.usernameUnavailable(
                    user,
                    request.username(),
                    usernameService.generateSuggestions(
                            request.username()
                    )
            );
        }

        user.setDisplayName(
                request.displayName()
        );

        user.setUsername(
                request.username()
                        .trim()
                        .toLowerCase()
        );

        user.setRegistrationStage(
                RegistrationStage.PROFILE_COMPLETED
        );

        userRepository.save(user);

        log.info(
                "Profile completed successfully for user {}",
                user.getId()
        );

        return AuthMapper.toCompleteProfileResponse(
                user,
                "Profile completed successfully."
        );
    }

    @Override
    public LoginResponse login(
            LoginRequest request,
            HttpServletRequest httpRequest
    ) {

        User user = userRepository
                .findByEmailAndDeletedAtIsNull(request.email())
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid email or password."
                        ));

        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException(
                    "Please verify your email first."
            );
        }

        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new InvalidAccountStateException(
                    "Your account is not active."
            );
        }

        if (!passwordService.matches(
                request.password(),
                user.getPasswordHash()
        )) {

            throw new InvalidCredentialsException(
                    "Invalid email or password."
            );
        }

        refreshTokenService.revokeActiveSession(
                user.getId(),
                request.deviceId()
        );

        String userAgent = httpRequest.getHeader("User-Agent");

        String ipAddress = extractClientIp(httpRequest);

        DeviceInfo deviceInfo =
                new DeviceInfo(
                        request.deviceId(),
                        request.deviceName(),
                        request.platform(),
                        userAgent,
                        ipAddress
                );

        RefreshTokenResult refreshResult =
                refreshTokenService.create(
                        user,
                        deviceInfo
                );

        String accessToken = createAccessToken(user, refreshResult.refreshToken().getId());

        log.info("User {} logged in successfully.", user.getEmail());

        return AuthMapper.toLoginResponse(
                accessToken,
                refreshResult.rawToken(),
                jwtProperties.getAccessTokenExpiration().getSeconds(),
                jwtProperties.getRefreshTokenExpiration().getSeconds(),
                user
        );
    }

    @Override
    @Transactional
    public LoginResponse googleLogin(
            GoogleLoginRequest request,
            String userAgent,
            String ipAddress
    ) {

        GooglePrincipal googleUser =
                googleService.verifyIdToken(
                        request.idToken()
                );

        User user;

        Optional<AuthAccount> googleAccount =
                authAccountRepository.findByProviderAndProviderUserId(
                        AuthProvider.GOOGLE,
                        googleUser.subject()
                );

        if (googleAccount.isPresent()) {

            user = googleAccount.get().getUser();

        } else {

            user = userRepository
                    .findByEmailAndDeletedAtIsNull(
                            googleUser.email()
                    )
                    .orElseGet(() -> {

                        User newUser =
                                User.builder()
                                        .email(googleUser.email())
                                        .displayName(googleUser.name())
                                        .profilePictureUrl(googleUser.picture())
                                        .emailVerified(true)
                                        .registrationStage(
                                                RegistrationStage.EMAIL_VERIFIED
                                        )
                                        .accountStatus(
                                                AccountStatus.ACTIVE
                                        )
                                        .role(Role.USER)
                                        .visibility(Visibility.PUBLIC)
                                        .notificationsEnabled(true)
                                        .twoFactorEnabled(false)
                                        .build();

                        return userRepository.save(newUser);

                    });

            AuthAccount account =
                    AuthAccount.builder()
                            .user(user)
                            .provider(AuthProvider.GOOGLE)
                            .providerUserId(
                                    googleUser.subject()
                            )
                            .email(
                                    googleUser.email()
                            )
                            .build();

            authAccountRepository.save(account);
        }

        if (user.getDeletedAt() != null) {
            throw new InvalidCredentialsException(
                    "Account has been deleted."
            );
        }

        if (user.getAccountStatus() == AccountStatus.DEACTIVATED) {
            throw new InvalidCredentialsException(
                    "Your account is inactive."
            );
        }

        if (user.getAccountStatus() == AccountStatus.SUSPENDED) {
            throw new InvalidCredentialsException(
                    "Your account has been suspended."
            );
        }

        refreshTokenService.revokeActiveSession(
                user.getId(),
                request.deviceId()
        );

        DeviceInfo deviceInfo =
                new DeviceInfo(
                        request.deviceId(),
                        request.deviceName(),
                        request.platform(),
                        userAgent,
                        ipAddress
                );

        RefreshTokenResult refreshToken = refreshTokenService.create(user, deviceInfo);

        String accessToken = createAccessToken(user, refreshToken.refreshToken().getId());

        log.info(
                "User {} logged in using Google.",
                user.getEmail()
        );

        return new LoginResponse(
                accessToken,
                refreshToken.rawToken(),
                jwtProperties.getAccessTokenExpiration().getSeconds(),
                jwtProperties.getRefreshTokenExpiration().getSeconds(),
                AuthMapper.toUserResponse(user)
        );

    }
    
    @Override
    public LoginResponse refresh(
            RefreshTokenRequest request
    ) {

        String refreshToken = request.refreshToken();

        if (!jwtService.isTokenValid(refreshToken)) {
            throw new InvalidTokenException("Invalid refresh token.");
        }

        UUID userId = jwtService.extractUserId(refreshToken);

        UUID sessionId = jwtService.extractSessionId(refreshToken);

        RefreshToken storedToken = refreshTokenService.findActive(sessionId);

        if (!refreshTokenService.matches(storedToken, refreshToken)) {

            throw new InvalidTokenException("Invalid refresh token.");

        }

        User user =
                userRepository
                        .findByIdAndDeletedAtIsNull(userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found."
                                ));

        if (!user.isEmailVerified()) {
            throw new UnauthorizedException(
                    "Email not verified."
            );
        }

        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new UnauthorizedException(
                    "Account is inactive."
            );
        }

        RefreshTokenResult rotated =
                refreshTokenService.rotate(
                        storedToken,
                        user
                );

        String accessToken = createAccessToken(user, rotated.refreshToken().getId());

        return AuthMapper.toLoginResponse(
                accessToken,
                rotated.rawToken(),
                jwtProperties.getAccessTokenExpiration().getSeconds(),
                jwtProperties.getRefreshTokenExpiration().getSeconds(),
                user
        );

    }

    @Override
    public MessageResponse logout(LogoutRequest request) {

        UUID sessionId = jwtService.extractSessionId(request.refreshToken());

        RefreshToken refreshToken = refreshTokenService.findActive(sessionId);

        if (!refreshTokenService.matches(refreshToken, request.refreshToken())) {

            throw new InvalidTokenException("Invalid refresh token.");

        }

        refreshTokenService.revoke(refreshToken);

        log.info("Session {} logged out successfully.", sessionId);

        return new MessageResponse(
                "Logged out successfully."
        );

    }

    @Override
    public MessageResponse logoutAll(UUID userId) {

        refreshTokenService.revokeAll(userId);

        log.info("User {} logged out from all devices.", userId);

        return new MessageResponse(
                "Logged out from all devices successfully."
        );

    }

    @Override
    @Transactional(readOnly = true)
    public List<SessionResponse> getSessions(
            UUID userId,
            UUID currentSessionId
    ) {

        return refreshTokenService
                .findActiveSessions(userId)
                .stream()
                .map(token ->
                        AuthMapper.toSessionResponse(
                                token,
                                currentSessionId
                        )
                )
                .toList();

    }

    @Override
    public MessageResponse revokeSession(UUID userId, UUID sessionId) {
        refreshTokenService.revokeById(sessionId);
        log.info("Session {} revoked successfully for user {}.", sessionId, userId);
        return new MessageResponse("Session revoked successfully.");
    }

    @Override
    public MessageResponse forgotPassword(
            ForgotPasswordRequest request
    ) {

        userRepository
                .findByEmailAndDeletedAtIsNull(request.email())
                .ifPresent(passwordResetService::create);

        return new MessageResponse(
                "If an account exists for this email, a password reset code has been sent."
        );

    }

    @Override
    public MessageResponse resetPassword(
            ResetPasswordRequest request
    ) {

        User user =
                userRepository
                        .findByEmailAndDeletedAtIsNull(request.email())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Invalid password reset request."
                                ));

        PasswordResetToken token =
                passwordResetService.getActive(user);

        if (token.getExpiresAt().isBefore(Instant.now(clock))) {
            throw new InvalidVerificationCodeException(
                    "Password reset code has expired."
            );
        }

        if (token.getAttempts() >= authProperties.getMaxVerificationAttempts()) {
            throw new InvalidVerificationCodeException(
                    "Maximum verification attempts exceeded."
            );
        }

        boolean matches =
                passwordService.matches(
                        request.code(),
                        token.getVerificationCodeHash()
                );

        if (!matches) {

            passwordResetService.incrementAttempts(token);

            throw new InvalidVerificationCodeException(
                    "Invalid password reset code."
            );

        }

        passwordResetService.markVerified(token);

        user.setPasswordHash(
                passwordService.hash(request.newPassword())
        );

        userRepository.save(user);

        refreshTokenService.revokeAll(user.getId());

        eventPublisher.publishEvent(
                new PasswordChangedEvent(
                        user.getId(),
                        user.getEmail()
                )
        );

        return new MessageResponse(
                "Password reset successfully."
        );

    }

    @Override
    public MessageResponse resendPasswordResetOtp(
            ForgotPasswordRequest request
    ) {

        userRepository
                .findByEmailAndDeletedAtIsNull(request.email())
                .ifPresent(passwordResetService::resend);

        return new MessageResponse(
                "If an account exists for this email, a new password reset code has been sent."
        );

    }

    @Override
    @Transactional
    public MessageResponse changePassword(
            UUID userId,
            ChangePasswordRequest request
    ) {

        User user = userRepository
                .findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found.")

                );

        // Verify current password
        if (!passwordService.matches(
                request.currentPassword(),
                user.getPasswordHash()
        )) {
            throw new UnauthorizedException("Current password is incorrect.");
        }

        // Prevent using the same password
        if (passwordService.matches(
                request.newPassword(),
                user.getPasswordHash()
        )) {
            throw new DuplicateResourceException(
                    "New password must be different from the current password."
            );
        }
        // Update password
        user.setPasswordHash(passwordService.hash(request.newPassword()));

        userRepository.save(user);
        // Revoke every session
        refreshTokenService.revokeAll(user.getId());
        // Audit / notification event
        eventPublisher.publishEvent(
                new PasswordChangedEvent(
                        user.getId(),
                        user.getEmail()
                )
        );

        return new MessageResponse(
                "Password changed successfully."
        );

    }

    @Override
    public void deactivateAccount(
            UUID userId,
            ConfirmPasswordRequest request
    ) {

        User user =
                userRepository
                        .findByIdAndDeletedAtIsNull(userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found."
                                ));

        AuthAccount account =
                authAccountRepository
                        .findByUserIdAndProvider(
                                userId,
                                AuthProvider.EMAIL
                        )
                        .orElse(null);

        if (account != null &&
                !passwordService.matches(
                        request.password(),
                        user.getPasswordHash()
                )) {

            throw new InvalidCredentialsException(
                    "Incorrect password."
            );

        }

        refreshTokenService.revokeAll(userId);

        user.setAccountStatus(AccountStatus.DEACTIVATED);

        userRepository.save(user);

        eventPublisher.publishEvent(
                new AccountStatusChangedEvent(
                        user.getId(),
                        user.getEmail(),
                        AccountStatus.DEACTIVATED
                )
        );

        log.info("User {} deactivated account.", user.getEmail());

    }

    @Override
    public void deleteAccount(
            UUID userId,
            ConfirmPasswordRequest request
    ) {

        User user =
                userRepository
                        .findByIdAndDeletedAtIsNull(userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found."
                                ));

        AuthAccount account =
                authAccountRepository
                        .findByUserIdAndProvider(
                                userId,
                                AuthProvider.EMAIL
                        )
                        .orElse(null);

        if (account != null &&
                !passwordService.matches(
                        request.password(),
                        user.getPasswordHash()
                )) {

            throw new InvalidCredentialsException(
                    "Incorrect password."
            );

        }

        refreshTokenService.revokeAll(userId);

        businessService.deleteBusiness(userId);

        Instant now = Instant.now(clock);

        user.setDeletedAt(now);

        user.setAccountStatus(AccountStatus.DELETED);

        userRepository.save(user);

        eventPublisher.publishEvent(
                new AccountStatusChangedEvent(
                        user.getId(),
                        user.getEmail(),
                        AccountStatus.DELETED
                )
        );

        log.info("User {} deleted account.", user.getEmail());

    }

    private String extractClientIp(HttpServletRequest request) {

        String forwarded = request.getHeader("X-Forwarded-For");

        if (forwarded != null && !forwarded.isBlank()) {

            return forwarded.split(",")[0].trim();

        }

        return request.getRemoteAddr();
    }

    private String createAccessToken(
            User user,
            UUID sessionId
    ) {
        return jwtService.generateAccessToken(
                user.getId(),
                sessionId,
                user.getEmail(),
                Map.of(
                        "role", user.getRole().name(),
                        "registrationStage", user.getRegistrationStage().name()
                )
        );
    }

}