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

}
