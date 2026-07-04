package com.amos_tech_code.zoner.auth.dto.internal;

import com.amos_tech_code.zoner.common.enums.DevicePlatform;

public record DeviceInfo(

        String deviceId,

        String deviceName,

        DevicePlatform platform,

        String userAgent,

        String ipAddress

) {
}
