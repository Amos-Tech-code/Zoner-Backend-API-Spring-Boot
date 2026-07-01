package com.amos_tech_code.zoner.auth.entity;

import com.amos_tech_code.zoner.common.entity.BaseEntity;
import com.amos_tech_code.zoner.common.enums.DevicePlatform;
import com.amos_tech_code.zoner.users.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_refresh_user", columnList = "user_id"),
                @Index(name = "idx_refresh_expires", columnList = "expires_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RefreshToken extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
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

    private Instant revokedAt;

    private Instant lastUsedAt;
}
