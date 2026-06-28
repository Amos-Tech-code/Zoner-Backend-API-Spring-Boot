package com.amos_tech_code.zoner.auth.util;

import java.time.Duration;
import java.time.Instant;

public final class VerificationCodeUtils {

    private VerificationCodeUtils() {
    }

    public static Instant expiry(Duration duration) {
        return Instant.now().plus(duration);
    }

}
