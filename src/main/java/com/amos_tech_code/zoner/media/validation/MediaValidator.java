package com.amos_tech_code.zoner.media.validation;

import com.amos_tech_code.zoner.common.exception.InvalidMediaException;
import com.amos_tech_code.zoner.media.enums.UploadFolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class MediaValidator {

    public void validate(
            MultipartFile file,
            UploadFolder folder
    ) {

        if (file.isEmpty()) {
            throw new InvalidMediaException(
                    "File cannot be empty."
            );
        }

        if (file.getContentType() == null) {
            throw new InvalidMediaException(
                    "Unknown content type."
            );
        }

        switch (folder) {

            case USER_PROFILE ->
                    validateImage(
                            file,
                            5
                    );

            case BUSINESS_LOGO ->
                    validateImage(
                            file,
                            5
                    );

            case BUSINESS_COVER ->
                    validateImage(
                            file,
                            10
                    );

            case POST_IMAGE ->
                    validateImage(
                            file,
                            20
                    );

            case STORY_IMAGE ->
                    validateImage(
                            file,
                            20
                    );

            case PRODUCT_IMAGE ->
                    validateImage(
                            file,
                            15
                    );

            case MESSAGE_IMAGE ->
                    validateImage(
                            file,
                            10
                    );

            case POST_VIDEO,
                 STORY_VIDEO,
                 PRODUCT_VIDEO,
                 MESSAGE_VIDEO ->
                    validateVideo(
                            file,
                            100
                    );

            default ->
                    throw new InvalidMediaException(
                            "Unsupported upload type."
                    );
        }

    }

    private void validateImage(
            MultipartFile file,
            int maxMb
    ) {

        validateSize(file, maxMb);

        if (!file.getContentType().startsWith("image/")) {
            throw new InvalidMediaException(
                    "Image expected."
            );
        }

    }

    private void validateVideo(
            MultipartFile file,
            int maxMb
    ) {

        validateSize(file, maxMb);

        if (!file.getContentType().startsWith("video/")) {
            throw new InvalidMediaException(
                    "Video expected."
            );
        }

    }

    private void validateSize(
            MultipartFile file,
            int maxMb
    ) {

        long maxBytes = maxMb * 1024L * 1024L;

        if (file.getSize() > maxBytes) {
            throw new InvalidMediaException(
                    "Maximum upload size is "
                            + maxMb
                            + " MB."
            );
        }

    }

}
