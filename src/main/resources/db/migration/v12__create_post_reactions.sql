CREATE TABLE post_reactions
(
    id          UUID PRIMARY KEY,

    user_id     UUID NOT NULL,

    post_id     UUID NOT NULL,

    type        VARCHAR(30) NOT NULL,

    created_at  TIMESTAMPTZ NOT NULL,

    updated_at  TIMESTAMPTZ NOT NULL,

    deleted_at  TIMESTAMPTZ,

    version     BIGINT NOT NULL,

    CONSTRAINT fk_reaction_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_reaction_post
        FOREIGN KEY (post_id)
            REFERENCES posts(id)
            ON DELETE CASCADE,

    CONSTRAINT uk_post_reaction_user_post
        UNIQUE (user_id, post_id)
);

CREATE INDEX idx_post_reaction_post
    ON post_reactions(post_id);

CREATE INDEX idx_post_reaction_user
    ON post_reactions(user_id);

CREATE INDEX idx_post_reaction_deleted
    ON post_reactions(deleted_at);