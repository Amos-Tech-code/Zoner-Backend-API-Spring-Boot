package com.amos_tech_code.zoner.social.post.dto.request

import com.amos_tech_code.zoner.social.post.enums.PostVisibility
import jakarta.validation.constraints.Size
import java.util.UUID

data class UpdatePostRequest(

    @field:Size(max = 5000)
    val caption: String?,

    val visibility: PostVisibility,

    val commentsEnabled: Boolean,

    val allowSharing: Boolean,

    val mediaIds: List<UUID>

)