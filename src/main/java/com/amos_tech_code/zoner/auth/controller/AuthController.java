package com.amos_tech_code.zoner.auth.controller;

import com.amos_tech_code.zoner.auth.dto.request.*;
import com.amos_tech_code.zoner.auth.dto.response.*;
import com.amos_tech_code.zoner.auth.service.AuthService;
import com.amos_tech_code.zoner.security.AuthenticatedUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return authService.register(request);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<VerifyEmailResponse> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request
    ) {

        VerifyEmailResponse response = authService.verifyEmail(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-verify-email")
    public ResponseEntity<RegisterResponse> resendVerifyEmail(
            @Valid @RequestBody ResendVerificationRequest request
    ) {
        RegisterResponse response = authService.resendVerificationCode(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/complete-profile")
    public ResponseEntity<CompleteProfileResponse> completeProfile(
            @Valid @RequestBody CompleteProfileRequest request
    ) {

        return ResponseEntity.ok(authService.completeProfile(request));

    }

    @PostMapping("/google")
    public LoginResponse googleLogin(
            @Valid @RequestBody GoogleLoginRequest request,
            HttpServletRequest servletRequest
    ) {

        return authService.googleLogin(
                request,
                servletRequest.getHeader("User-Agent"),
                servletRequest.getRemoteAddr()
        );

    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {

        return ResponseEntity.ok(authService.login(request, httpRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {

        return ResponseEntity.ok(authService.refresh(request));
    }

    @GetMapping("/sessions")
    public List<SessionResponse> sessions(
            @AuthenticationPrincipal
            AuthenticatedUser principal

    ) {

        return authService.getSessions(
                principal.id(),
                principal.sessionId()
        );

    }

    @DeleteMapping("/sessions/{sessionId}")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponse revokeSession(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID sessionId
    ) {
        return authService.revokeSession(principal.id(), sessionId);
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(
            @Valid @RequestBody LogoutRequest request
    ) {

        return ResponseEntity.ok(
                authService.logout(request)
        );

    }

    @PostMapping("/logout-all")
    public ResponseEntity<MessageResponse> logoutAll(
            @AuthenticationPrincipal
            AuthenticatedUser user

    ) {

        return ResponseEntity.ok(
                authService.logoutAll(user.id())
        );

    }

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponse forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {

        return authService.forgotPassword(request);

    }

    @PostMapping("/resend-password-reset")
    @ResponseStatus(HttpStatus.OK)
        public MessageResponse resendPasswordReset(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {

        return authService.resendPasswordResetOtp(request);

    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponse resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {

        return authService.resetPassword(request);

    }

    @PostMapping("/change-password")
    public ResponseEntity<MessageResponse> changePassword(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody ChangePasswordRequest request
    ) {

        return ResponseEntity.ok(
                authService.changePassword(user.id(), request)
        );

    }

    @PatchMapping("/me/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateAccount(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody ConfirmPasswordRequest request
    ) {

        authService.deactivateAccount(
                user.id(),
                request
        );

    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody ConfirmPasswordRequest request
    ) {

        authService.deleteAccount(
                user.id(),
                request
        );

    }



}