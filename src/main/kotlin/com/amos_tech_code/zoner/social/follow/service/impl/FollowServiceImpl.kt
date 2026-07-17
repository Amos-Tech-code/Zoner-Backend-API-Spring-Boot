package com.amos_tech_code.zoner.social.follow.service.impl

import com.amos_tech_code.zoner.business.repository.BusinessProfileRepository
import com.amos_tech_code.zoner.common.exception.ResourceNotFoundException
import com.amos_tech_code.zoner.social.follow.dto.request.FollowRequest
import com.amos_tech_code.zoner.social.follow.dto.response.FollowResponse
import com.amos_tech_code.zoner.social.follow.dto.response.FollowStatsResponse
import com.amos_tech_code.zoner.social.follow.dto.response.FollowUserResponse
import com.amos_tech_code.zoner.social.follow.dto.response.FollowingResponse
import com.amos_tech_code.zoner.social.follow.entity.Follow
import com.amos_tech_code.zoner.social.follow.enums.FollowTargetType
import com.amos_tech_code.zoner.social.follow.event.UserFollowedEvent
import com.amos_tech_code.zoner.social.follow.event.UserUnfollowedEvent
import com.amos_tech_code.zoner.social.follow.repository.FollowRepository
import com.amos_tech_code.zoner.social.follow.service.FollowService
import com.amos_tech_code.zoner.users.repository.UserRepository
import org.apache.coyote.BadRequestException
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class FollowServiceImpl(

    private val repository: FollowRepository,

    private val userRepository: UserRepository,

    private val businessRepository: BusinessProfileRepository,

    private val eventPublisher: ApplicationEventPublisher

) : FollowService {

    override fun follow(
        followerId: UUID,
        request: FollowRequest
    ): FollowResponse {

        validateTarget(followerId, request)

        if (
            repository.existsByFollowerIdAndTargetTypeAndTargetId(
                followerId,
                request.targetType,
                request.targetId)
        ) {
            return FollowResponse(true)
        }

        val follower = userRepository.findByIdAndDeletedAtIsNull(followerId)
            .orElseThrow { ResourceNotFoundException("Follower not found") }

        val follow = Follow(
            follower = follower,
            targetType = request.targetType,
            targetId = request.targetId
        )

        repository.save(follow)

        eventPublisher.publishEvent(
            UserFollowedEvent(
                followerId,
                request.targetType,
                request.targetId

            )
        )

        return FollowResponse(true)
    }

    override fun unfollow(
        followerId: UUID,
        request: FollowRequest
    ) {
        repository.deleteByFollowerIdAndTargetTypeAndTargetId(
            followerId,
            request.targetType,
            request.targetId
        )

        eventPublisher.publishEvent(
            UserUnfollowedEvent(
                followerId,
                request.targetType,
                request.targetId

            )
        )
    }


    override fun isFollowing(
        followerId: UUID,
        request: FollowRequest
    ): Boolean {

        return repository.existsByFollowerIdAndTargetTypeAndTargetId(
            followerId,
            request.targetType,
            request.targetId
        )

    }

    override fun getFollowers(
        targetType: FollowTargetType,
        targetId: UUID,
        pageable: Pageable
    ): Page<FollowUserResponse> {

        val followersPage = repository.findByTargetTypeAndTargetId(
            targetType,
            targetId,
            pageable
        )

        return followersPage.map { follow ->
            FollowUserResponse(
                id = follow.follower.id,
                username = follow.follower.username,
                displayName = follow.follower.displayName,
                profilePictureUrl = follow.follower.profilePicture.secureUrl,
                followedAt = follow.createdAt
            )
        }


    }

    override fun getFollowing(
        followerId: UUID,
        pageable: Pageable
    ): Page<FollowingResponse> {

        val followingPage = repository.findByFollowerId(
            followerId,
            pageable
        )

        return followingPage.map { follow ->
            when (follow.targetType) {
                FollowTargetType.USER -> {
                    val user = userRepository.findByIdAndDeletedAtIsNull(follow.targetId)
                        .orElseThrow { ResourceNotFoundException("User not found for targetId: ${follow.targetId}") }
                    FollowingResponse(
                        id = user.id,
                        type = FollowTargetType.USER,
                        name = user.displayName ?: user.username ?: "Unknown User",
                        imageUrl = user.profilePicture.secureUrl
                    )
                }

                FollowTargetType.BUSINESS -> {
                    val business = businessRepository.findById(follow.targetId)
                        .orElseThrow { ResourceNotFoundException("Business not found for targetId: ${follow.targetId}") }
                    FollowingResponse(
                        id = business.id,
                        type = FollowTargetType.BUSINESS,
                        name = business.businessName,
                        imageUrl = business.logo.secureUrl
                    )
                }
            }
        }

    }

    override fun getStats(userId: UUID): FollowStatsResponse {

        return FollowStatsResponse(
            followers =
                repository.countByTargetTypeAndTargetId(
                    FollowTargetType.USER,
                    userId
                ),
            following =
                repository.countByFollowerId(
                    userId
                )
        )

    }


    private fun validateTarget(
        followerId: UUID,
        request: FollowRequest
    ) {

        when (request.targetType) {

            FollowTargetType.USER -> {

                if (request.targetId == followerId) {

                    throw BadRequestException("You cannot follow yourself.")

                }

                if (!userRepository.existsById(request.targetId)) {

                    throw ResourceNotFoundException("User not found.")

                }

            }

            FollowTargetType.BUSINESS -> {

                if (!businessRepository.existsById(request.targetId)) {

                    throw ResourceNotFoundException("Business not found.")

                }

            }

        }

    }


}