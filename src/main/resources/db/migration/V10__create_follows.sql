CREATE TABLE follows
(
    id UUID PRIMARY KEY,

    follower_id UUID NOT NULL,

    target_type VARCHAR(30) NOT NULL,

    target_id UUID NOT NULL,

    created_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT fk_follow_follower
        FOREIGN KEY (follower_id)
            REFERENCES users(id)
            ON DELETE CASCADE
);

CREATE UNIQUE INDEX uk_follow_unique
    ON follows(
               follower_id,
               target_type,
               target_id
        );

CREATE INDEX idx_follow_follower
    ON follows(follower_id);

CREATE INDEX idx_follow_target
    ON follows(target_type,target_id);

CREATE INDEX idx_follow_created
    ON follows(created_at);