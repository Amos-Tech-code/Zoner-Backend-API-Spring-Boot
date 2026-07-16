package com.amos_tech_code.zoner.media.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AttachMediaRequest(

        @NotNull
        UUID mediaId

) {}
