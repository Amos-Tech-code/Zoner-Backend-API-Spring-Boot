package com.amos_tech_code.zoner.social.follow.dto.response

import com.amos_tech_code.zoner.social.follow.enums.FollowTargetType
import java.util.UUID

data class FollowingResponse(

    val id: UUID,

    val type: FollowTargetType,

    val name: String,

    val imageUrl: String?

)