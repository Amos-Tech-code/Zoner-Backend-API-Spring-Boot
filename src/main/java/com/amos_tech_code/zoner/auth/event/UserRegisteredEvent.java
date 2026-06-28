package com.amos_tech_code.zoner.auth.event;

import java.util.UUID;

public record UserRegisteredEvent(

        UUID userId,

        String email,

        String verificationCode

) {
}