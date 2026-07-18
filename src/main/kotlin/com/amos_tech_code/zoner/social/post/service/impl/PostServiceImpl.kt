package com.amos_tech_code.zoner.social.post.service.impl

import com.amos_tech_code.zoner.business.entity.BusinessProfile
import com.amos_tech_code.zoner.business.repository.BusinessProfileRepository
import com.amos_tech_code.zoner.common.exception.InvalidRequestException
import com.amos_tech_code.zoner.common.exception.ResourceNotFoundException
import com.amos_tech_code.zoner.common.exception.UnauthorizedException
import com.amos_tech_code.zoner.media.entity.Media
import com.amos_tech_code.zoner.media.enums.MediaOwnerType
import com.amos_tech_code.zoner.media.enums.MediaStatus
import com.amos_tech_code.zoner.media.repository.MediaRepository
import com.amos_tech_code.zoner.social.post.dto.request.CreatePostRequest
import com.amos_tech_code.zoner.social.post.dto.request.UpdatePostRequest
import com.amos_tech_code.zoner.social.post.dto.response.PostResponse
import com.amos_tech_code.zoner.social.post.entity.Post
import com.amos_tech_code.zoner.social.post.enums.PostStatus
import com.amos_tech_code.zoner.social.post.event.PostDraftCreatedEvent
import com.amos_tech_code.zoner.social.post.event.PostPublishedEvent
import com.amos_tech_code.zoner.social.post.mapper.PostMapper
import com.amos_tech_code.zoner.social.post.repository.PostRepository
import com.amos_tech_code.zoner.social.post.service.PostService
import com.amos_tech_code.zoner.users.repository.UserRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Instant
import java.util.UUID

class PostServiceImpl(
    private val postRepository: PostRepository,

    private val businessRepository: BusinessProfileRepository,

    private val userRepository: UserRepository,

    private val mediaRepository: MediaRepository,

    private val eventPublisher: ApplicationEventPublisher,

    private val clock: Clock
) : PostService {

    @Override
    @Transactional
    override fun create(
        userId: UUID,
        request: CreatePostRequest
    ): PostResponse {

        val business = getBusiness(userId)

        if (request.mediaIds.size > 10) {
            throw InvalidRequestException(
                "A post can contain a maximum of 10 media files."
            )
        }

        val media = validateAndLoadMedia(
            userId,
            request.mediaIds
        )

        val now = Instant.now(clock)

        val post = Post(
            business = business,
            caption = request.caption?.trim(),
            visibility = request.visibility,
            status = request.status,
            commentsEnabled = request.commentsEnabled,
            allowSharing = request.allowSharing,
            editedAt = null,
            publishedAt =
                if (request.status == PostStatus.PUBLISHED)
                    now
                else
                    null
        )

        postRepository.save(post)

        attachMedia(
            post,
            media
        )

        if (post.status == PostStatus.PUBLISHED) {

            eventPublisher.publishEvent(
                PostPublishedEvent(
                    post.id
                )
            )

        } else {

            eventPublisher.publishEvent(
                PostDraftCreatedEvent(
                    post.id
                )
            )

        }

        return PostMapper.toResponse(
            post,
            media
        )

    }

    override fun update(
        userId: UUID,
        postId: UUID,
        request: UpdatePostRequest
    ): PostResponse {
        TODO("Not yet implemented")
    }

    override fun publish(
        userId: UUID,
        postId: UUID
    ): PostResponse {
        TODO("Not yet implemented")
    }

    override fun delete(userId: UUID, postId: UUID) {
        TODO("Not yet implemented")
    }

    override fun get(postId: UUID): PostResponse {
        TODO("Not yet implemented")
    }

    override fun getBusinessPosts(
        businessId: UUID,
        pageable: Pageable
    ): Page<PostResponse> {
        TODO("Not yet implemented")
    }

    override fun getDrafts(
        userId: UUID,
        pageable: Pageable
    ): Page<PostResponse> {
        TODO("Not yet implemented")
    }

    private fun getBusiness(
        userId: UUID
    ): BusinessProfile {

        val user = userRepository
            .findByIdAndDeletedAtIsNull(userId)
            .orElseThrow {
                ResourceNotFoundException("User not found.")
            }

        return businessRepository
            .findByUserIdAndDeletedAtIsNull(user.id)
            .orElseThrow {
                UnauthorizedException(
                    "Only business accounts can create posts."
                )
            }

    }

    private fun validateAndLoadMedia(
        userId: UUID,
        mediaIds: List<UUID>
    ): MutableList<Media> {

        if (mediaIds.isEmpty()) {
            return mutableListOf()
        }

        val media =
            mediaRepository
                .findByIdInAndOwnerTypeAndOwnerIdAndStatus(
                    mediaIds,
                    MediaOwnerType.POST,
                    userId,
                    MediaStatus.TEMPORARY
                )
                .toMutableList()

        if (media.size != mediaIds.size) {

            throw InvalidRequestException(
                "One or more uploaded media files are invalid."
            )

        }

        return media

    }

    private fun attachMedia(
        post: Post,
        media: List<Media>
    ) {

        media.forEachIndexed { index, item ->

            item.ownerType = MediaOwnerType.POST

            item.ownerId = post.id

            item.status = MediaStatus.ACTIVE

            item.displayOrder = index

        }

        mediaRepository.saveAll(media)

    }

}