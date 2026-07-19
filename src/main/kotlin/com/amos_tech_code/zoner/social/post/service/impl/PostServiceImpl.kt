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
import com.amos_tech_code.zoner.social.post.event.PostDeletedEvent
import com.amos_tech_code.zoner.social.post.event.PostDraftCreatedEvent
import com.amos_tech_code.zoner.social.post.event.PostPublishedEvent
import com.amos_tech_code.zoner.social.post.event.PostUpdatedEvent
import com.amos_tech_code.zoner.social.post.mapper.PostMapper
import com.amos_tech_code.zoner.social.post.repository.PostRepository
import com.amos_tech_code.zoner.social.post.service.PostService
import com.amos_tech_code.zoner.users.repository.UserRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Instant
import java.util.UUID

@Service
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
            publishedAt = if (request.status == PostStatus.PUBLISHED) now else null
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

    @Override
    @Transactional
    override fun update(
        userId: UUID,
        postId: UUID,
        request: UpdatePostRequest
    ): PostResponse {

        val business = getBusiness(userId)

        val post = postRepository
            .findByIdAndDeletedAtIsNull(postId)
            .orElseThrow {
                ResourceNotFoundException("Post not found.")
            }

        if (post.business!!.id != business.id) {
            throw UnauthorizedException(
                "You are not allowed to edit this post."
            )
        }

        if (post.status == PostStatus.DELETED) {
            throw InvalidRequestException(
                "Deleted posts cannot be edited."
            )
        }

        val media = validateMediaForUpdate(
            userId,
            post,
            request.mediaIds
        )

        post.caption = request.caption?.trim()
        post.visibility = request.visibility
        post.commentsEnabled = request.commentsEnabled
        post.allowSharing = request.allowSharing
        post.editedAt = Instant.now(clock)

        postRepository.save(post)

        syncMedia(
            post,
            media
        )

        eventPublisher.publishEvent(
            PostUpdatedEvent(
                post.id
            )
        )

        return PostMapper.toResponse(
            post,
            media.sortedBy { it.displayOrder }
        )

    }

    @Override
    @Transactional
    override fun publish(
        userId: UUID,
        postId: UUID
    ): PostResponse {

        val business = getBusiness(userId)

        val post = postRepository
            .findByIdAndDeletedAtIsNull(postId)
            .orElseThrow {
                ResourceNotFoundException("Post not found.")
            }

        if (post.business!!.id != business.id) {
            throw UnauthorizedException(
                "You cannot publish this post."
            )
        }

        if (post.status == PostStatus.PUBLISHED) {
            throw InvalidRequestException(
                "Post is already published."
            )
        }

        post.status = PostStatus.PUBLISHED
        post.publishedAt = Instant.now(clock)

        postRepository.save(post)

        eventPublisher.publishEvent(
            PostPublishedEvent(post.id)
        )

        val media =
            mediaRepository
                .findByOwnerTypeAndOwnerIdAndStatusOrderByDisplayOrderAsc(
                    MediaOwnerType.POST,
                    post.id,
                    MediaStatus.ACTIVE
                )

        return PostMapper.toResponse(
            post,
            media
        )

    }

    @Override
    @Transactional
    override fun delete(
        userId: UUID,
        postId: UUID
    ) {

        val business = getBusiness(userId)

        val post = postRepository
            .findByIdAndDeletedAtIsNull(postId)
            .orElseThrow {
                ResourceNotFoundException("Post not found.")
            }

        if (post.business!!.id != business.id) {
            throw UnauthorizedException(
                "You cannot delete this post."
            )
        }

        post.deletedAt = Instant.now(clock)
        post.status = PostStatus.DELETED

        postRepository.save(post)

        eventPublisher.publishEvent(
            PostDeletedEvent(post.id)
        )

    }

    @Override
    @Transactional(readOnly = true)
    override fun get(
        postId: UUID
    ): PostResponse {

        val post = postRepository
            .findByIdAndDeletedAtIsNull(postId)
            .orElseThrow {
                ResourceNotFoundException("Post not found.")
            }

        val media =
            mediaRepository
                .findByOwnerTypeAndOwnerIdAndStatusOrderByDisplayOrderAsc(
                    MediaOwnerType.POST,
                    post.id,
                    MediaStatus.ACTIVE
                )

        return PostMapper.toResponse(
            post,
            media
        )

    }

    @Override
    @Transactional(readOnly = true)
    override fun getBusinessPosts(
        businessId: UUID,
        pageable: Pageable
    ): Page<PostResponse> {

        val business = businessRepository
            .findById(businessId)
            .orElseThrow {
                ResourceNotFoundException("Business not found.")
            }

        return postRepository
            .findByBusinessAndStatusAndDeletedAtIsNullOrderByPublishedAtDesc(
                business,
                PostStatus.PUBLISHED,
                pageable
            )
            .map { post ->

                val media =
                    mediaRepository
                        .findByOwnerTypeAndOwnerIdAndStatusOrderByDisplayOrderAsc(
                            MediaOwnerType.POST,
                            post.id,
                            MediaStatus.ACTIVE
                        )

                PostMapper.toResponse(
                    post,
                    media
                )

            }

    }

    @Override
    @Transactional(readOnly = true)
    override fun getDrafts(
        userId: UUID,
        pageable: Pageable
    ): Page<PostResponse> {

        val business = getBusiness(userId)

        return postRepository
            .findByBusinessAndStatusAndDeletedAtIsNullOrderByUpdatedAtDesc(
                business,
                PostStatus.DRAFT,
                pageable
            )
            .map { post ->

                val media =
                    mediaRepository
                        .findByOwnerTypeAndOwnerIdAndStatusOrderByDisplayOrderAsc(
                            MediaOwnerType.POST,
                            post.id,
                            MediaStatus.ACTIVE
                        )

                PostMapper.toResponse(
                    post,
                    media
                )

            }

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

    private fun validateMediaForUpdate(
        userId: UUID,
        post: Post,
        mediaIds: List<UUID>
    ): MutableList<Media> {

        if (mediaIds.isEmpty()) {
            return mutableListOf()
        }

        val media = mediaRepository
            .findAllById(mediaIds)
            .toMutableList()

        if (media.size != mediaIds.size) {
            throw InvalidRequestException(
                "Some media files do not exist."
            )
        }

        media.forEach {

            when {

                // already attached to this post
                it.ownerType == MediaOwnerType.POST &&
                        it.ownerId == post.id -> Unit

                // newly uploaded by this user
                it.ownerType == MediaOwnerType.POST &&
                        (it.ownerId == userId || it.ownerId == post.id) &&
                        it.status == MediaStatus.TEMPORARY -> Unit

                else ->
                    throw InvalidRequestException(
                        "Invalid media attachment."
                    )

            }

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

    private fun syncMedia(
        post: Post,
        requestedMedia: List<Media>
    ) {

        val existingMedia =
            mediaRepository
                .findByOwnerTypeAndOwnerIdAndStatusOrderByDisplayOrderAsc(
                    MediaOwnerType.POST,
                    post.id,
                    MediaStatus.ACTIVE
                )

        val requestedIds = requestedMedia
                .map { it.id }
                .toSet()

        existingMedia
            .filter { it.id !in requestedIds }
            .forEach {

                it.ownerType = MediaOwnerType.USER
                it.ownerId = post.business!!.user.id
                it.status = MediaStatus.TEMPORARY

            }

        requestedMedia.forEachIndexed { index, media ->

            media.ownerType = MediaOwnerType.POST
            media.ownerId = post.id
            media.status = MediaStatus.ACTIVE
            media.displayOrder = index

        }

        mediaRepository.saveAll(
            existingMedia + requestedMedia
        )

    }

}