package com.amos_tech_code.zoner.social.follow.dto.response

import java.time.Instant
import java.util.UUID

data class FollowUserResponse(

    val id: UUID,

    val username: String?,

    val displayName: String?,

    val profilePictureUrl: String?,

    val followedAt: Instant

)