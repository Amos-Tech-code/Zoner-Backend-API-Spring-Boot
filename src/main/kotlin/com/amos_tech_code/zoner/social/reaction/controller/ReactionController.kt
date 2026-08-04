package com.amos_tech_code.zoner.social.reaction.controller

import com.amos_tech_code.zoner.security.AuthenticatedUser
import com.amos_tech_code.zoner.social.reaction.dto.response.ReactionResponse
import com.amos_tech_code.zoner.social.reaction.dto.response.ToggleReactionResponse
import com.amos_tech_code.zoner.social.reaction.service.ReactionService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/posts")
class ReactionController(

    private val reactionService: ReactionService

) {

    @PostMapping("/{postId}/reactions")
    fun toggleReaction(

        @AuthenticationPrincipal
        user: AuthenticatedUser,

        @PathVariable
        postId: UUID

    ): ToggleReactionResponse {

        return reactionService.toggleReaction(
            user.id(),
            postId
        )

    }

    @GetMapping("/{postId}/reactions")
    fun getReactions(

        @PathVariable
        postId: UUID,

        pageable: Pageable

    ): Page<ReactionResponse> {

        return reactionService.getPostReactions(
            postId,
            pageable
        )

    }

    @GetMapping("/{postId}/reactions/me")
    fun hasReacted(

        @AuthenticationPrincipal
        user: AuthenticatedUser,

        @PathVariable
        postId: UUID

    ): Boolean {

        return reactionService.hasReacted(
            user.id(),
            postId
        )

    }

}