package com.amos_tech_code.zoner.media.dto.internal;

import com.amos_tech_code.zoner.media.enums.MediaOwnerType;
import com.amos_tech_code.zoner.media.enums.MediaResourceType;
import com.amos_tech_code.zoner.media.enums.UploadFolder;

import java.util.UUID;

public record UploadOptions(

        UploadFolder folder,

        MediaOwnerType ownerType,

        UUID ownerId,

        MediaResourceType resourceType,

        Integer displayOrder

) {
}
