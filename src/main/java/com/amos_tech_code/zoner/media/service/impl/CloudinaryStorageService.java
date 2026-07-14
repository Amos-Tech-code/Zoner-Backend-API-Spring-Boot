package com.amos_tech_code.zoner.media.service.impl;

import com.amos_tech_code.zoner.common.exception.MediaDeleteException;
import com.amos_tech_code.zoner.common.exception.MediaUploadException;
import com.amos_tech_code.zoner.media.dto.internal.UploadOptions;
import com.amos_tech_code.zoner.media.dto.internal.StoredMedia;
import com.amos_tech_code.zoner.media.service.StorageService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryStorageService implements StorageService {

    private final Cloudinary cloudinary;

    @Override
    public StoredMedia upload(
            MultipartFile file,
            UploadOptions options
    ) {

        try {

            /**
             * This will be implemented later to transform files
             * based on upload type.
            **/
            /*
            Map<String, Object> uploadOptions =
                    transformationFactory.create(
                            options.folder()
                    );

            Map<?, ?> result =
                    cloudinary.uploader().upload(
                            file.getBytes(),
                            uploadOptions
                    );
            */
            Map<?, ?> result =
                    cloudinary.uploader().upload(
                            file.getBytes(),
                            ObjectUtils.asMap(
                                    "folder", options.folder().getFolder(),
                                    "resource_type", options.resourceType()
                                            .name()
                                            .toLowerCase()
                            )
                    );

            return StoredMedia.builder()
                    .publicId((String) result.get("public_id"))
                    .url((String) result.get("url"))
                    .secureUrl((String) result.get("secure_url"))
                    .format((String) result.get("format"))
                    .mimeType(file.getContentType())
                    .bytes(((Number) result.get("bytes")).longValue())
                    .width(
                            result.get("width") == null
                                    ? null : ((Number) result.get("width")).intValue()
                    )
                    .height(
                            result.get("height") == null
                                    ? null : ((Number) result.get("height")).intValue()
                    )
                    .duration(
                            result.get("duration") == null
                                    ? null
                                    : ((Number) result.get("duration")).doubleValue()
                    )
                    .build();

        } catch (Exception ex) {

            log.error("Cloudinary upload failed.", ex);

            throw new MediaUploadException(
                    "Failed to upload media."
            );

        }

    }

    @Override
    public void delete(
            String publicId
    ) {

        try {

            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

        } catch (Exception ex) {

            log.error("Cloudinary delete failed.", ex);

            throw new MediaDeleteException(
                    "Failed to delete media."
            );

        }

    }

}