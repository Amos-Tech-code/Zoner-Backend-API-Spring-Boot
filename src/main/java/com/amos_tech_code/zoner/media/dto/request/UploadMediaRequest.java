package com.amos_tech_code.zoner.media.dto.request;

import com.amos_tech_code.zoner.media.enums.MediaOwnerType;
import com.amos_tech_code.zoner.media.enums.MediaResourceType;
import com.amos_tech_code.zoner.media.enums.UploadFolder;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UploadMediaRequest(

        @NotNull
        UploadFolder folder,

        @NotNull
        MediaOwnerType ownerType,

        @NotNull
        MediaResourceType resourceType,

        UUID ownerId,

        Integer displayOrder

) {}