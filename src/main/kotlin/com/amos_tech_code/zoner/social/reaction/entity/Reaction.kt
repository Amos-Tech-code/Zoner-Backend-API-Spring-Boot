package com.amos_tech_code.zoner.social.reaction.entity

import com.amos_tech_code.zoner.common.entity.BaseEntity
import com.amos_tech_code.zoner.social.post.entity.Post
import com.amos_tech_code.zoner.social.reaction.enums.ReactionType
import com.amos_tech_code.zoner.users.entity.User
import jakarta.persistence.*

@Entity
@Table(
    name = "post_reactions",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_post_reaction_user_post",
            columnNames = [
                "user_id",
                "post_id"
            ]
        )
    ],
    indexes = [

        Index(
            name = "idx_post_reaction_post",
            columnList = "post_id"
        ),

        Index(
            name = "idx_post_reaction_user",
            columnList = "user_id"
        )

    ]
)
class Reaction(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    var post: Post,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: ReactionType

) : BaseEntity()