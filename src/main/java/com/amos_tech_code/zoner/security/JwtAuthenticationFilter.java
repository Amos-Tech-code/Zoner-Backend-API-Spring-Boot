package com.amos_tech_code.zoner.security;

import com.amos_tech_code.zoner.auth.service.JwtService;
import com.amos_tech_code.zoner.users.entity.User;
import com.amos_tech_code.zoner.users.enums.AccountStatus;
import com.amos_tech_code.zoner.users.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);

            return;
        }

        String token = authorizationHeader.substring(7);

        try {

            if (!jwtService.isTokenValid(token)) {

                filterChain.doFilter(request, response);

                return;

            }

            UUID userId = jwtService.extractUserId(token);
            UUID sessionId = jwtService.extractSessionId(token);

            User user =
                    userRepository
                            .findByIdAndDeletedAtIsNull(userId)
                            .orElse(null);

            if (!canAuthenticate(user)) {

                filterChain.doFilter(request, response);

                return;

            }

            AuthenticatedUser principal =
                    new AuthenticatedUser(
                            user.getId(),
                            sessionId,
                            user.getEmail(),
                            user.getRole()
                    );

            JwtAuthenticationToken authentication =
                    new JwtAuthenticationToken(
                            principal
                    );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

        } catch (Exception ex) {

            log.warn("JWT authentication failed: {}", ex.getMessage());

            SecurityContextHolder.clearContext();

        }

        filterChain.doFilter(request, response);

    }

    private boolean canAuthenticate(User user) {

        return user != null
                && user.isEmailVerified()
                && user.getAccountStatus() == AccountStatus.ACTIVE;

    }

}
