package com.amos_tech_code.zoner.devices.entity;

import com.amos_tech_code.zoner.common.enums.DevicePlatform;
import com.amos_tech_code.zoner.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "device_tokens",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_device_tokens_user_device",
                        columnNames = {
                                "user_id",
                                "device_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_device_tokens_user",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_device_tokens_fcm",
                        columnList = "fcm_token"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceToken {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String deviceId;

    @Column(nullable = false, unique = true)
    private String fcmToken;

    @Column
    private String deviceName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DevicePlatform platform;

    @Column(length = 50)
    private String appVersion;

    @Column(length = 150)
    private String deviceModel;

    @Column(nullable = false)
    private Instant lastSeenAt;

    @Column(nullable = false)
    private Instant createdAt;
}
