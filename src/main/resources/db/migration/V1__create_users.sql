CREATE TABLE users
(
    id                     UUID PRIMARY KEY,

    email                  VARCHAR(255) NOT NULL,

    password_hash          VARCHAR(255),

    username              VARCHAR(50),

    display_name          VARCHAR(100),

    bio                   TEXT,

    profile_picture_media_id UUID,

    phone                 VARCHAR(30),

    role                  VARCHAR(30) NOT NULL,

    registration_stage    VARCHAR(50) NOT NULL,

    account_status        VARCHAR(30) NOT NULL,

    visibility            VARCHAR(30) NOT NULL,

    email_verified        BOOLEAN NOT NULL DEFAULT FALSE,

    notifications_enabled BOOLEAN NOT NULL DEFAULT TRUE,

    two_factor_enabled    BOOLEAN NOT NULL DEFAULT FALSE,

    created_at            TIMESTAMPTZ NOT NULL,

    updated_at            TIMESTAMPTZ NOT NULL,

    deleted_at            TIMESTAMPTZ,

    version               BIGINT NOT NULL,

    CONSTRAINT fk_users_profile_picture
        FOREIGN KEY (profile_picture_media_id)
            REFERENCES media(id)

);

ALTER TABLE users
    ADD CONSTRAINT uk_users_email
        UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uk_users_username
        UNIQUE (username);

CREATE INDEX idx_users_email
    ON users(email);

CREATE INDEX idx_users_username
    ON users(username);

CREATE INDEX idx_users_registration_stage
    ON users(registration_stage);

CREATE INDEX idx_users_account_status
    ON users(account_status);

CREATE INDEX idx_users_deleted_at
    ON users(deleted_at);