package com.amos_tech_code.zoner.social.post.dto.response

import com.amos_tech_code.zoner.media.enums.MediaResourceType
import java.util.UUID

data class PostMediaResponse(

    val id: UUID,

    val secureUrl: String,

    val resourceType: MediaResourceType,

    val width: Int?,

    val height: Int?,

    val duration: Double?,

    val displayOrder: Int

)