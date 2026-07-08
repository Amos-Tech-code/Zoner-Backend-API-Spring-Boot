package com.amos_tech_code.zoner.business.dto.request;

import java.util.UUID;

public record SearchBusinessesRequest(

        String query,

        UUID categoryId,

        String city,

        String county,

        Boolean verified,

        Boolean featured

) {
}
