package com.amos_tech_code.zoner.auth.dto.response;

public record LoginResponse(

        String accessToken,

        String refreshToken,

        Long accessTokenExpiresIn,

        Long refreshTokenExpiresIn,

        UserResponse user

) {
}
