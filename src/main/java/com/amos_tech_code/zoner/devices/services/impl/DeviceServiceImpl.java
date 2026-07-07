package com.amos_tech_code.zoner.devices.services.impl;

import com.amos_tech_code.zoner.common.exception.ResourceNotFoundException;
import com.amos_tech_code.zoner.devices.dto.request.RegisterDeviceRequest;
import com.amos_tech_code.zoner.devices.dto.response.DeviceResponse;
import com.amos_tech_code.zoner.devices.entity.DeviceToken;
import com.amos_tech_code.zoner.devices.mappers.DeviceMapper;
import com.amos_tech_code.zoner.devices.repository.DeviceTokenRepository;
import com.amos_tech_code.zoner.devices.services.DeviceService;
import com.amos_tech_code.zoner.users.entity.User;
import com.amos_tech_code.zoner.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeviceServiceImpl implements DeviceService {

    private final DeviceTokenRepository deviceTokenRepository;

    private final UserRepository userRepository;

    private final Clock clock;

    @Override
    public void register(
            UUID userId,
            RegisterDeviceRequest request
    ) {

        User user = userRepository
                .findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found."
                        ));

        deviceTokenRepository
                .findByFcmToken(request.fcmToken())
                .ifPresent(existing -> {

                    if (!existing.getUser().getId().equals(userId)) {
                        deviceTokenRepository.delete(existing);
                    }

                });

        DeviceToken device =
                deviceTokenRepository
                        .findByUserIdAndDeviceId(
                                userId,
                                request.deviceId()
                        )
                        .orElse(null);

        if (device == null) {

            device = DeviceToken.builder()
                    .user(user)
                    .deviceId(request.deviceId())
                    .createdAt(Instant.now(clock))
                    .build();

        }

        device.setFcmToken(request.fcmToken());
        device.setDeviceName(request.deviceName());
        device.setDeviceModel(request.deviceModel());
        device.setPlatform(request.platform());
        device.setAppVersion(request.appVersion());
        device.setLastSeenAt(Instant.now(clock));

        deviceTokenRepository.save(device);

    }

    @Override
    public void unregister(
            UUID userId,
            String deviceId
    ) {

        deviceTokenRepository.deleteByUserIdAndDeviceId(
                userId,
                deviceId
        );

    }

    @Override
    @Transactional(readOnly = true)
    public List<DeviceResponse> getDevices(
            UUID userId
    ) {

        return deviceTokenRepository
                .findAllByUserId(userId)
                .stream()
                .map(DeviceMapper::toDeviceResponse)
                .toList();

    }

}
