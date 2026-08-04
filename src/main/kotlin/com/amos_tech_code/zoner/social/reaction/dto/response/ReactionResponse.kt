package com.amos_tech_code.zoner.social.reaction.dto.response

import com.amos_tech_code.zoner.social.reaction.enums.ReactionType
import java.time.Instant
import java.util.UUID

data class ReactionResponse(

    val id: UUID,

    val userId: UUID,

    val username: String?,

    val displayName: String?,

    val profilePicture: String?,

    val type: ReactionType,

    val reactedAt: Instant

)