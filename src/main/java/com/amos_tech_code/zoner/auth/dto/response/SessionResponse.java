package com.amos_tech_code.zoner.auth.dto.response;

import com.amos_tech_code.zoner.common.enums.DevicePlatform;

import java.time.Instant;
import java.util.UUID;

public record SessionResponse(

        UUID sessionId,

        String deviceName,

        String deviceId,

        DevicePlatform platform,

        String ipAddress,

        String userAgent,

        Instant createdAt,

        Instant lastUsedAt,

        Instant expiresAt,

        boolean current

) {}
