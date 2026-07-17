package com.amos_tech_code.zoner.social.follow.dto.response

import java.time.Instant
import java.util.UUID

data class FollowBusinessResponse(

    val id: UUID,

    val businessName: String,

    val logoUrl: String?,

    val followedAt: Instant

)