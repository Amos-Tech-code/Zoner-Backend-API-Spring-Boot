package com.amos_tech_code.zoner.media.service.impl;

import com.amos_tech_code.zoner.common.exception.InvalidRequestException;
import com.amos_tech_code.zoner.common.exception.ResourceNotFoundException;
import com.amos_tech_code.zoner.media.dto.response.MediaResponse;
import com.amos_tech_code.zoner.media.dto.internal.UploadOptions;
import com.amos_tech_code.zoner.media.dto.internal.StoredMedia;
import com.amos_tech_code.zoner.media.entity.Media;
import com.amos_tech_code.zoner.media.enums.MediaOwnerType;
import com.amos_tech_code.zoner.media.enums.MediaStatus;
import com.amos_tech_code.zoner.media.mapper.MediaMapper;
import com.amos_tech_code.zoner.media.repository.MediaRepository;
import com.amos_tech_code.zoner.media.service.MediaService;
import com.amos_tech_code.zoner.media.service.StorageService;
import com.amos_tech_code.zoner.media.validation.MediaValidator;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MediaServiceImpl implements MediaService {

    private final MediaRepository repository;

    private final StorageService storageService;

    private final MediaValidator mediaValidator;

    private final Clock clock;

    @Override
    public MediaResponse upload(
            MultipartFile file,
            UploadOptions options
    ) {

        mediaValidator.validate(file, options.folder());

        StoredMedia stored =
                storageService.upload(
                        file,
                        options
                );

        Media media =
                Media.builder()
                        .publicId(stored.publicId())
                        .url(stored.url())
                        .secureUrl(stored.secureUrl())
                        .resourceType(options.resourceType())
                        .mimeType(stored.mimeType())
                        .format(stored.format())
                        .bytes(stored.bytes())
                        .width(stored.width())
                        .height(stored.height())
                        .duration(stored.duration())
                        .folder(options.folder())
                        .ownerType(options.ownerType())
                        .ownerId(options.ownerId())
                        .displayOrder(options.displayOrder())
                        .status(MediaStatus.TEMPORARY)
                        .createdAt(Instant.now(clock))
                        .build();

        repository.save(media);

        return MediaMapper.toResponse(media);

    }

    @Override
    @Transactional(readOnly = true)
    public MediaResponse getById(
            UUID id
    ) {

        Optional<Media> media =
                repository.findById(id);

        if (media.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Media not found."
            );
        } else {
            return MediaMapper.toResponse(media.get());
        }

    }

    @Override
    @Transactional(readOnly = true)
    public List<MediaResponse> findByOwner(
            MediaOwnerType ownerType,
            UUID ownerId
    ) {

        return repository
                .findByOwnerTypeAndOwnerIdAndDeletedAtIsNullOrderByDisplayOrderAsc(
                        ownerType,
                        ownerId
                )
                .stream()
                .map(MediaMapper::toResponse)
                .toList();

    }

    @Override
    public void delete(
            UUID mediaId
    ) {

        Media media =
                repository.findById(mediaId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Media not found."
                                ));

        if (media.getDeletedAt() != null) {
            return;
        }

        media.setDeletedAt(
                Instant.now(clock)
        );

        repository.save(media);

    }

    @Override
    public Media attach(
            UUID mediaId,
            MediaOwnerType ownerType,
            UUID ownerId
    ) {

        Media media =
                repository.findById(mediaId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Media not found."
                                ));

        if (media.getDeletedAt() != null) {
            throw new ResourceNotFoundException(
                    "Media not found."
            );
        }

        if (media.getStatus() != MediaStatus.TEMPORARY) {
            throw new InvalidRequestException(
                    "This media is not temporary. Please upload again."
            );
        }

        if (media.getOwnerType() != ownerType) {
            throw new InvalidRequestException(
                    "Media owner type mismatch."
            );
        }

        media.setOwnerId(ownerId);
        media.setStatus(MediaStatus.ACTIVE);

        return repository.save(media);

    }

    @Override
    public void attachAll(
            List<UUID> mediaIds,
            MediaOwnerType ownerType,
            UUID ownerId
    ) {

//        mediaIds.forEach(id ->
//
//                attach(
//                        id,
//                        ownerType,
//                        ownerId
//                )
//
//        );

    }

}
