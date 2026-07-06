package com.amos_tech_code.zoner.auth.event;

import java.util.UUID;

public record PasswordResetRequestedEvent(

        UUID userId,

        String email,

        String code

) {
}
