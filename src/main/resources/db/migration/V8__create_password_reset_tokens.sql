CREATE TABLE password_reset_tokens
(
    id                  UUID PRIMARY KEY,

    user_id             UUID NOT NULL,

    verification_code_hash VARCHAR(255) NOT NULL,

    expires_at          TIMESTAMPTZ NOT NULL,

    attempts            INTEGER NOT NULL DEFAULT 0,

    verified_at         TIMESTAMPTZ,

    CONSTRAINT fk_password_reset_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE
);

----------------------------------------------------------
-- Indexes
----------------------------------------------------------

CREATE INDEX idx_password_reset_user
    ON password_reset_tokens(user_id);

CREATE INDEX idx_password_reset_expires
    ON password_reset_tokens(expires_at);