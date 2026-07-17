package com.amos_tech_code.zoner.social.follow.dto.request

import com.amos_tech_code.zoner.social.follow.enums.FollowTargetType
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class FollowRequest(

    @field:NotNull(message = "Target type cannot be null")
    val targetType: FollowTargetType,

    @field:NotNull(message = "Target ID cannot be null")
    val targetId: UUID

)