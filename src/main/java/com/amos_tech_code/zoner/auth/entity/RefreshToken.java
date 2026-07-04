package com.amos_tech_code.zoner.auth.entity;

import com.amos_tech_code.zoner.common.enums.DevicePlatform;
import com.amos_tech_code.zoner.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_refresh_user", columnList = "user_id"),
                @Index(name = "idx_refresh_expires", columnList = "expires_at"),
                @Index(name = "idx_refresh_revoked", columnList = "revoked_at"),
                @Index(name = "idx_refresh_device", columnList = "device_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 64)
    private String tokenHash;

    @Column
    private String deviceId;

    @Column
    private String deviceName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DevicePlatform platform;

    @Column(columnDefinition = "TEXT")
    private String userAgent;

    @Column(length = 100)
    private String ipAddress;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private Instant createdAt;

    @Column
    private Instant revokedAt;

    @Column
    private Instant lastUsedAt;
}
