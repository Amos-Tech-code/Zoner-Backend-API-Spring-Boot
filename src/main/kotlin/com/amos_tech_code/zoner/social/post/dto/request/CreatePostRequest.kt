package com.amos_tech_code.zoner.social.post.dto.request

import com.amos_tech_code.zoner.social.post.enums.PostStatus
import com.amos_tech_code.zoner.social.post.enums.PostVisibility
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.UUID

data class CreatePostRequest(

    @field:Size(max = 5000)
    val caption: String?,

    @field:NotBlank(message = "Post visibility is required")
    val visibility: PostVisibility,

    @field:NotBlank(message = "Post status is required")
    val status: PostStatus,

    val commentsEnabled: Boolean = true,

    val allowSharing: Boolean = true,

    val mediaIds: List<UUID> = emptyList()

)