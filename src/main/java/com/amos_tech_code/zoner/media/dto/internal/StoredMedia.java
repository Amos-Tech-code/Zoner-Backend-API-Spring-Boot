package com.amos_tech_code.zoner.media.dto.internal;

import lombok.Builder;

@Builder
public record StoredMedia(

        String publicId,

        String url,

        String secureUrl,

        String format,

        String mimeType,

        long bytes,

        int width,

        int height,

        Double duration

) {}
