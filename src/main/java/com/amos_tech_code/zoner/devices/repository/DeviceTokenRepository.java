package com.amos_tech_code.zoner.devices.repository;

import com.amos_tech_code.zoner.devices.entity.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, UUID> {

    Optional<DeviceToken> findByUserIdAndDeviceId(
            UUID userId,
            String deviceId
    );

    List<DeviceToken> findAllByUserId(UUID userId);

    Optional<DeviceToken> findByFcmToken(String fcmToken);

    void deleteByUserIdAndDeviceId(
            UUID userId,
            String deviceId
    );

}
