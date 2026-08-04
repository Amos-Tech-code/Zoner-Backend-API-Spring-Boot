package com.amos_tech_code.zoner.social.reaction.service.impl

import com.amos_tech_code.zoner.common.exception.InvalidRequestException
import com.amos_tech_code.zoner.common.exception.ResourceNotFoundException
import com.amos_tech_code.zoner.social.post.entity.Post
import com.amos_tech_code.zoner.social.post.enums.PostStatus
import com.amos_tech_code.zoner.social.post.repository.PostRepository
import com.amos_tech_code.zoner.social.reaction.dto.response.ReactionResponse
import com.amos_tech_code.zoner.social.reaction.dto.response.ToggleReactionResponse
import com.amos_tech_code.zoner.social.reaction.entity.Reaction
import com.amos_tech_code.zoner.social.reaction.enums.ReactionType
import com.amos_tech_code.zoner.social.reaction.event.ReactionCreatedEvent
import com.amos_tech_code.zoner.social.reaction.event.ReactionRemovedEvent
import com.amos_tech_code.zoner.social.reaction.mapper.ReactionMapper
import com.amos_tech_code.zoner.social.reaction.repository.ReactionRepository
import com.amos_tech_code.zoner.social.reaction.service.ReactionService
import com.amos_tech_code.zoner.users.entity.User
import com.amos_tech_code.zoner.users.repository.UserRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class ReactionServiceImpl(

    private val reactionRepository: ReactionRepository,

    private val userRepository: UserRepository,

    private val postRepository: PostRepository,

    private val eventPublisher: ApplicationEventPublisher

) : ReactionService {

    override fun toggleReaction(
        userId: UUID,
        postId: UUID
    ): ToggleReactionResponse {

        val user = userRepository
            .findByIdAndDeletedAtIsNull(userId)
            .orElseThrow {
                ResourceNotFoundException("User not found.")
            }

        val post = postRepository
            .findByIdAndDeletedAtIsNull(postId)
            .orElseThrow {
                ResourceNotFoundException("Post not found.")
            }

        if (post.status != PostStatus.PUBLISHED) {
            throw InvalidRequestException(
                "Only published posts can be liked."
            )
        }

        val existingReaction =
            reactionRepository.findByUserAndPost(
                user,
                post
            )

        return if (existingReaction.isPresent) {

            removeReaction(
                existingReaction.get(),
                post
            )

        } else {

            createReaction(
                user,
                post
            )

        }

    }

    @Transactional(readOnly = true)
    override fun getPostReactions(
        postId: UUID,
        pageable: Pageable
    ): Page<ReactionResponse> {

        val post = postRepository
            .findByIdAndDeletedAtIsNull(postId)
            .orElseThrow {
                ResourceNotFoundException("Post not found.")
            }

        return reactionRepository
            .findByPost(
                post,
                pageable
            )
            .map(
                ReactionMapper::toResponse
            )

    }

    @Transactional(readOnly = true)
    override fun hasReacted(
        userId: UUID,
        postId: UUID
    ): Boolean {

        val user = userRepository
            .findByIdAndDeletedAtIsNull(userId)
            .orElseThrow {
                ResourceNotFoundException("User not found.")
            }

        val post = postRepository
            .findByIdAndDeletedAtIsNull(postId)
            .orElseThrow {
                ResourceNotFoundException("Post not found.")
            }

        return reactionRepository.existsByUserAndPost(
            user,
            post
        )

    }

    private fun createReaction(
        user: User,
        post: Post
    ): ToggleReactionResponse {

        val reaction = Reaction(
            user = user,
            post = post,
            type = ReactionType.LIKE
        )

        reactionRepository.save(reaction)

        post.likesCount++

        postRepository.save(post)

        eventPublisher.publishEvent(
            ReactionCreatedEvent(
                user.id,
                post.id
            )
        )

        return ToggleReactionResponse(
            liked = true,
            likesCount = post.likesCount
        )

    }

    private fun removeReaction(
        reaction: Reaction,
        post: Post
    ): ToggleReactionResponse {

        reactionRepository.delete(reaction)

        if (post.likesCount > 0) {
            post.likesCount--
        }

        postRepository.save(post)

        eventPublisher.publishEvent(
            ReactionRemovedEvent(
                reaction.user.id,
                post.id
            )
        )

        return ToggleReactionResponse(
            liked = false,
            likesCount = post.likesCount
        )

    }

}