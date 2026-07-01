package com.amos_tech_code.zoner.security;

import com.amos_tech_code.zoner.users.enums.Role;

import java.util.UUID;

public record AuthenticatedUser(

        UUID id,

        String email,

        Role role

) {
}
