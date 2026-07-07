package com.amos_tech_code.zoner.devices.dto.response;

import com.amos_tech_code.zoner.common.enums.DevicePlatform;

import java.time.Instant;
import java.util.UUID;

public record DeviceResponse(

        UUID id,

        String deviceId,

        String deviceName,

        String deviceModel,

        DevicePlatform platform,

        String appVersion,

        Instant lastSeenAt

) {
}
