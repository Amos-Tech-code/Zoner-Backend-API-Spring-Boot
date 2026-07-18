package com.amos_tech_code.zoner.media.repository;

import com.amos_tech_code.zoner.media.entity.Media;
import com.amos_tech_code.zoner.media.enums.MediaOwnerType;
import com.amos_tech_code.zoner.media.enums.MediaStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MediaRepository extends JpaRepository<Media, UUID> {

    List<Media> findByOwnerTypeAndOwnerIdAndDeletedAtIsNullOrderByDisplayOrderAsc(
            MediaOwnerType ownerType,
            UUID ownerId
    );

    List<Media> findByIdInAndOwnerTypeAndOwnerIdAndStatus(
            Collection<UUID> ids,
            MediaOwnerType ownerType,
            UUID ownerId,
            MediaStatus status
    );

    List<Media> findByOwnerTypeAndOwnerIdAndStatusOrderByDisplayOrderAsc(
            MediaOwnerType ownerType,
            UUID ownerId,
            MediaStatus status
    );

}