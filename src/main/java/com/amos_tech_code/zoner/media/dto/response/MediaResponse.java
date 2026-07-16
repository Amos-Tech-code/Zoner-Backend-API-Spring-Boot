package com.amos_tech_code.zoner.media.dto.response;

import com.amos_tech_code.zoner.media.enums.MediaResourceType;

import java.util.UUID;

public record MediaResponse(

        UUID id,

        String url,

        String secureUrl,

        MediaResourceType resourceType,

        String mimeType,

        Long bytes,

        Integer width,

        Integer height,

        Double duration,

        Integer displayOrder

) {}
