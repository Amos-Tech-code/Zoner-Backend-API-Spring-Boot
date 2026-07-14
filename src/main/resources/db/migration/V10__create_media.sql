CREATE TABLE media
(
    id UUID PRIMARY KEY,

    public_id VARCHAR(255) NOT NULL UNIQUE,

    secure_url TEXT NOT NULL,

    url TEXT NOT NULL,

    resource_type VARCHAR(20) NOT NULL,

    mime_type VARCHAR(100) NOT NULL,

    format VARCHAR(20) NOT NULL,

    bytes BIGINT NOT NULL,

    width INTEGER,

    height INTEGER,

    duration DOUBLE PRECISION,

    folder VARCHAR(255) NOT NULL,

    owner_type VARCHAR(30) NOT NULL,

    owner_id UUID NOT NULL,

    display_order INTEGER NOT NULL DEFAULT 0,

    created_at TIMESTAMPTZ NOT NULL,

    deleted_at TIMESTAMPTZ
);

CREATE INDEX idx_media_owner
    ON media(owner_type, owner_id);

CREATE INDEX idx_media_deleted
    ON media(deleted_at);

CREATE UNIQUE INDEX idx_media_public_id
    ON media(public_id);