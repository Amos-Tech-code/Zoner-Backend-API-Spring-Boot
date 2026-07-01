package com.amos_tech_code.zoner.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.List;

public class JwtAuthenticationToken
        extends AbstractAuthenticationToken {

    private final AuthenticatedUser principal;

    public JwtAuthenticationToken(
            AuthenticatedUser principal
    ) {

        super(List.of());

        this.principal = principal;

        setAuthenticated(true);

    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

}