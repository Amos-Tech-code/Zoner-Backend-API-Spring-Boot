package com.amos_tech_code.zoner.social.reaction.mapper

import com.amos_tech_code.zoner.social.reaction.dto.response.ReactionResponse
import com.amos_tech_code.zoner.social.reaction.entity.Reaction

object ReactionMapper {

    fun toResponse(
        reaction: Reaction
    ) = ReactionResponse(

        id = reaction.id,

        userId = reaction.user.id,

        username = reaction.user.username,

        displayName = reaction.user.displayName,

        profilePicture = reaction.user.profilePicture?.secureUrl,

        type = reaction.type,

        reactedAt = reaction.createdAt

    )

}