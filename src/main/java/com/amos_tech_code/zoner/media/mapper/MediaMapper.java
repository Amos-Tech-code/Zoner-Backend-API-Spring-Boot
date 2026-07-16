package com.amos_tech_code.zoner.media.mapper;

import com.amos_tech_code.zoner.media.dto.response.MediaResponse;
import com.amos_tech_code.zoner.media.entity.Media;

public final class MediaMapper {

    private MediaMapper() {
    }

    public static MediaResponse toResponse(
            Media media
    ) {

        return new MediaResponse(
                media.getId(),
                media.getUrl(),
                media.getSecureUrl(),
                media.getResourceType(),
                media.getMimeType(),
                media.getBytes(),
                media.getWidth(),
                media.getHeight(),
                media.getDuration(),
                media.getDisplayOrder()
        );

    }

}
