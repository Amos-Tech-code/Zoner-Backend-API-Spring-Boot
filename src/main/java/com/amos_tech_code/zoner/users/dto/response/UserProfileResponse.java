package com.amos_tech_code.zoner.users.dto.response;

import java.util.UUID;

public record UserProfileResponse(

        UUID id,

        String displayName,

        String username,

        String bio,

        String profilePictureUrl,

        String phone,

        boolean emailVerified

) {}
