CREATE TABLE auth_accounts
(
    id                 UUID PRIMARY KEY,

    user_id            UUID NOT NULL,

    provider           VARCHAR(20) NOT NULL,

    provider_user_id   VARCHAR(255) NOT NULL,

    email              VARCHAR(255) NOT NULL,

    created_at         TIMESTAMPTZ NOT NULL,

    updated_at         TIMESTAMPTZ NOT NULL,

    deleted_at         TIMESTAMPTZ,

    version            BIGINT NOT NULL,

    CONSTRAINT fk_auth_accounts_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE
);

ALTER TABLE auth_accounts
    ADD CONSTRAINT uk_auth_provider_account
        UNIQUE (provider, provider_user_id);

CREATE INDEX idx_auth_accounts_user
    ON auth_accounts(user_id);

CREATE INDEX idx_auth_accounts_provider
    ON auth_accounts(provider);

CREATE INDEX idx_auth_accounts_deleted_at
    ON auth_accounts(deleted_at);