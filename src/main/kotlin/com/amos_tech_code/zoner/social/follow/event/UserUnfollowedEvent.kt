package com.amos_tech_code.zoner.social.follow.event

import com.amos_tech_code.zoner.social.follow.enums.FollowTargetType
import java.util.UUID

data class UserUnfollowedEvent(

    val followerId: UUID,

    val targetType: FollowTargetType,

    val targetId: UUID

)