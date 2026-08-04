package com.amos_tech_code.zoner.social.post.dto.response

import com.amos_tech_code.zoner.social.post.enums.PostStatus
import com.amos_tech_code.zoner.social.post.enums.PostVisibility
import java.time.Instant
import java.util.UUID

data class PostResponse(

    val id: UUID,

    val business: BusinessSummary,

    val caption: String?,

    val visibility: PostVisibility,

    val status: PostStatus,

    val commentsEnabled: Boolean,

    val allowSharing: Boolean,

    val media: List<PostMediaResponse>,

    val commentsCount: Long,

    val likesCount: Long,

    val bookmarksCount: Long,

    val viewsCount: Long,

    val repostsCount: Long,

    /*
    val likedByMe: Boolean,

    val bookmarkedByMe: Boolean,

    val repostedByMe: Boolean,
     */

    val createdAt: Instant,

    val updatedAt: Instant,

    val editedAt: Instant?,

    val publishedAt: Instant?

)