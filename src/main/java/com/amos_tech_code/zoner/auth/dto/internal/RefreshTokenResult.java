package com.amos_tech_code.zoner.auth.dto.internal;

import com.amos_tech_code.zoner.auth.entity.RefreshToken;

public record RefreshTokenResult(

        RefreshToken refreshToken,

        String rawToken

) {
}
