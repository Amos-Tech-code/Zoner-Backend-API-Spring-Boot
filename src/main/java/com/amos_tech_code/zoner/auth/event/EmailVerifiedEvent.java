package com.amos_tech_code.zoner.auth.event;

import java.util.UUID;

public record EmailVerifiedEvent(
        UUID userId,
        String email
) {
}
