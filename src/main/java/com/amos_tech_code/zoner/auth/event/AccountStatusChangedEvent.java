package com.amos_tech_code.zoner.auth.event;

import com.amos_tech_code.zoner.users.enums.AccountStatus;

import java.util.UUID;

public record AccountStatusChangedEvent(
        UUID userId,
        String email,
        AccountStatus status
) {}
