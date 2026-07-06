package com.amos_tech_code.zoner.auth.event;

import java.util.UUID;

public record PasswordChangedEvent(
        UUID userId,
        String email
) {
}
