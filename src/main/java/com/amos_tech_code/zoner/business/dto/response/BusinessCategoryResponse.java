package com.amos_tech_code.zoner.business.dto.response;

import java.util.UUID;

public record BusinessCategoryResponse(

        UUID id,

        String name,

        String description,

        String iconUrl

) {}
