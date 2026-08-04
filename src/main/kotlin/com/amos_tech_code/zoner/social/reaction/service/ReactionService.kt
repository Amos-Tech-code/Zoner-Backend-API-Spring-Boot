package com.amos_tech_code.zoner.social.reaction.service

import com.amos_tech_code.zoner.social.reaction.dto.response.ReactionResponse
import com.amos_tech_code.zoner.social.reaction.dto.response.ToggleReactionResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface ReactionService {

    fun toggleReaction(
        userId: UUID,
        postId: UUID
    ): ToggleReactionResponse

    fun getPostReactions(
        postId: UUID,
        pageable: Pageable
    ): Page<ReactionResponse>

    fun hasReacted(
        userId: UUID,
        postId: UUID
    ): Boolean

}