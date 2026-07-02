package com.amos_tech_code.zoner.auth.dto.response;

public record LoginResponse(

        String accessToken,

        Long expiresIn,

        String refreshToken,

        UserResponse user

) {}
