CREATE TABLE posts
(
    id UUID PRIMARY KEY,

    business_id UUID,

    caption TEXT,

    visibility VARCHAR(30) NOT NULL,

    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',

    comments_enabled BOOLEAN NOT NULL DEFAULT TRUE,

    allow_sharing BOOLEAN NOT NULL DEFAULT TRUE,

    edited_at TIMESTAMPTZ,

    published_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ NOT NULL,

    updated_at TIMESTAMPTZ NOT NULL,

    deleted_at TIMESTAMPTZ,

    version BIGINT NOT NULL,

    CONSTRAINT fk_posts_business
        FOREIGN KEY (business_id)
            REFERENCES business_profiles(id)
            ON DELETE SET NULL
);

CREATE INDEX idx_posts_business
    ON posts(business_id);

CREATE INDEX idx_posts_status
    ON posts(status);

CREATE INDEX idx_posts_visibility
    ON posts(visibility);

CREATE INDEX idx_posts_published_at
    ON posts(published_at DESC);

CREATE INDEX idx_posts_created_at
    ON posts(created_at DESC);

CREATE INDEX idx_posts_deleted_at
    ON posts(deleted_at);