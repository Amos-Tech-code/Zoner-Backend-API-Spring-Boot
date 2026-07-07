CREATE TABLE device_tokens
(
    id              UUID PRIMARY KEY,

    user_id         UUID NOT NULL,

    device_id       VARCHAR(255) NOT NULL,

    fcm_token       TEXT NOT NULL,

    device_name     VARCHAR(255),

    platform        VARCHAR(30) NOT NULL,

    app_version     VARCHAR(50),

    device_model    VARCHAR(150),

    last_seen_at    TIMESTAMPTZ NOT NULL,

    created_at      TIMESTAMPTZ NOT NULL,

    CONSTRAINT fk_device_tokens_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE
);

CREATE UNIQUE INDEX uq_device_tokens_user_device
    ON device_tokens(user_id, device_id);

CREATE UNIQUE INDEX uq_device_tokens_fcm
    ON device_tokens(fcm_token);

CREATE INDEX idx_device_tokens_user
    ON device_tokens(user_id);

CREATE INDEX idx_device_tokens_last_seen
    ON device_tokens(last_seen_at);