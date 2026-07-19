package com.amos_tech_code.zoner.social.post.event

import java.util.UUID

data class PostPublishedEvent(
    val postId: UUID
)

data class PostDraftCreatedEvent(
    val postId: UUID

)

data class PostUpdatedEvent(
    val postId: UUID
)

data class PostDeletedEvent(
    val postId: UUID
)