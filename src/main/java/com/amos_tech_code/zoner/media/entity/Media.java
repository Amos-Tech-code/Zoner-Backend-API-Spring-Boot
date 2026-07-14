package com.amos_tech_code.zoner.media.entity;

import com.amos_tech_code.zoner.media.enums.MediaOwnerType;
import com.amos_tech_code.zoner.media.enums.MediaResourceType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "media",
        indexes = {
                @Index(name = "idx_media_owner", columnList = "owner_type, owner_id"),
                @Index(name = "idx_media_deleted", columnList = "deleted_at"),
                @Index(name = "idx_media_public_id", columnList = "public_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Media {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, unique = true)
    private String publicId;

    @Column(nullable = false)
    private String secureUrl;

    @Column(nullable = false)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaResourceType resourceType;

    @Column(nullable = false)
    private String mimeType;

    @Column(nullable = false)
    private String format;

    @Column(nullable = false)
    private Long bytes;

    private Integer width;

    private Integer height;

    private Double duration;

    @Column(nullable = false)
    private String folder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaOwnerType ownerType;

    @Column(nullable = false)
    private UUID ownerId;

    @Column(nullable = false)
    private Integer displayOrder;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant deletedAt;
}
