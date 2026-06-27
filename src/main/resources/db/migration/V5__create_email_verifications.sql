CREATE TABLE email_verifications
(
    id                      UUID PRIMARY KEY,

    user_id                 UUID NOT NULL,

    verification_code_hash  VARCHAR(255) NOT NULL,

    expires_at              TIMESTAMPTZ NOT NULL,

    verified_at             TIMESTAMPTZ,

    attempts                INTEGER NOT NULL DEFAULT 0,

    created_at              TIMESTAMPTZ NOT NULL,

    updated_at              TIMESTAMPTZ NOT NULL,

    deleted_at              TIMESTAMPTZ,

    version                 BIGINT NOT NULL,

    CONSTRAINT fk_email_verifications_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE
);

CREATE INDEX idx_email_verifications_user
    ON email_verifications(user_id);

CREATE INDEX idx_email_verifications_expires_at
    ON email_verifications(expires_at);

CREATE INDEX idx_email_verifications_verified_at
    ON email_verifications(verified_at);

CREATE INDEX idx_email_verifications_deleted_at
    ON email_verifications(deleted_at);