package com.amos_tech_code.zoner.social.reaction.event

import java.util.UUID

data class ReactionRemovedEvent(

    val userId: UUID,

    val postId: UUID

)