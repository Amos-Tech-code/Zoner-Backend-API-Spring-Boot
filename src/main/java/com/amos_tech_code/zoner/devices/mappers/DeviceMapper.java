package com.amos_tech_code.zoner.devices.mappers;

import com.amos_tech_code.zoner.devices.dto.response.DeviceResponse;
import com.amos_tech_code.zoner.devices.entity.DeviceToken;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DeviceMapper {

    public static DeviceResponse toDeviceResponse(
            DeviceToken device
    ) {

        return new DeviceResponse(
                device.getId(),
                device.getDeviceId(),
                device.getDeviceName(),
                device.getDeviceModel(),
                device.getPlatform(),
                device.getAppVersion(),
                device.getLastSeenAt()
        );

    }

}