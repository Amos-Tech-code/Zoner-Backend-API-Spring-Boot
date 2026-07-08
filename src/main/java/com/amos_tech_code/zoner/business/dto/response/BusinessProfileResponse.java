package com.amos_tech_code.zoner.business.dto.response;

import java.util.UUID;

public record BusinessProfileResponse(

        UUID id,

        UUID userId,

        UUID categoryId,

        String category,

        String businessName,

        String description,

        String logoUrl,

        String coverPhotoUrl,

        String phone,

        String email,

        String website,

        String address,

        String city,

        String county,

        String country,

        String postalCode,

        boolean featured,

        boolean verified,

        boolean acceptsMessages

) {
}
