package com.amos_tech_code.zoner.security;

import com.amos_tech_code.zoner.common.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CurrentUserServiceImpl implements CurrentUserService {

    @Override
    public AuthenticatedUser getAuthenticatedUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal() instanceof AuthenticatedUser principal)) {

            throw new UnauthorizedException(
                    "Authentication required."
            );

        }

        return principal;

    }

    @Override
    public UUID getCurrentUserId() {

        return getAuthenticatedUser().id();

    }

}
