package com.amos_tech_code.zoner.devices.services;

import com.amos_tech_code.zoner.devices.dto.request.RegisterDeviceRequest;
import com.amos_tech_code.zoner.devices.dto.response.DeviceResponse;

import java.util.List;
import java.util.UUID;

public interface DeviceService {

    void register(
            UUID userId,
            RegisterDeviceRequest request
    );

    void unregister(
            UUID userId,
            String deviceId
    );

    List<DeviceResponse> getDevices(
            UUID userId
    );

}
