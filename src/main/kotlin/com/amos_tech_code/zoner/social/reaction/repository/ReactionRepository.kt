package com.amos_tech_code.zoner.social.reaction.repository

import com.amos_tech_code.zoner.social.post.entity.Post
import com.amos_tech_code.zoner.social.reaction.entity.Reaction
import com.amos_tech_code.zoner.users.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface ReactionRepository : JpaRepository<Reaction, UUID> {

    fun findByUserAndPost(
        user: User,
        post: Post
    ): Optional<Reaction>

    fun existsByUserAndPost(
        user: User,
        post: Post
    ): Boolean

    fun deleteByUserAndPost(
        user: User,
        post: Post
    )

    fun findByPost(
        post: Post,
        pageable: Pageable
    ): Page<Reaction>

    fun countByPost(
        post: Post
    ): Long

}