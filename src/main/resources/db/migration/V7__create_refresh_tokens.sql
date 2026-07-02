CREATE TABLE refresh_tokens
(
    id              UUID PRIMARY KEY,

    user_id         UUID NOT NULL,

    token_hash      VARCHAR(64) NOT NULL,

    device_id       VARCHAR(255),

    device_name     VARCHAR(255),

    platform        VARCHAR(30) NOT NULL,

    user_agent      TEXT,

    ip_address      VARCHAR(100),

    expires_at      TIMESTAMPTZ NOT NULL,

    revoked_at      TIMESTAMPTZ,

    last_used_at    TIMESTAMPTZ,

    CONSTRAINT fk_refresh_tokens_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE
);

------------------------------------------------------------
-- Indexes
------------------------------------------------------------

CREATE INDEX idx_refresh_tokens_user
    ON refresh_tokens(user_id);

CREATE INDEX idx_refresh_tokens_expires
    ON refresh_tokens(expires_at);

CREATE INDEX idx_refresh_tokens_revoked
    ON refresh_tokens(revoked_at);

CREATE INDEX idx_refresh_tokens_device
    ON refresh_tokens(device_id);

CREATE INDEX idx_refresh_tokens_last_used
    ON refresh_tokens(last_used_at);

------------------------------------------------------------
-- Prevent duplicate active sessions on the same device
------------------------------------------------------------

CREATE UNIQUE INDEX uq_refresh_tokens_user_device_active
    ON refresh_tokens(user_id, device_id)
    WHERE revoked_at IS NULL;