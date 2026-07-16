package com.amos_tech_code.zoner.media.service;

import com.amos_tech_code.zoner.media.dto.response.MediaResponse;
import com.amos_tech_code.zoner.media.dto.internal.UploadOptions;
import com.amos_tech_code.zoner.media.entity.Media;
import com.amos_tech_code.zoner.media.enums.MediaOwnerType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface MediaService {

    MediaResponse upload(
            MultipartFile file,
            UploadOptions options
    );

    MediaResponse getById(UUID id);

    List<MediaResponse> findByOwner(
            MediaOwnerType ownerType,
            UUID ownerId
    );

    void delete(UUID mediaId);

    Media attach(
            UUID mediaId,
            MediaOwnerType ownerType,
            UUID ownerId
    );

    void attachAll(
            List<UUID> mediaIds,
            MediaOwnerType ownerType,
            UUID ownerId
    );
}
