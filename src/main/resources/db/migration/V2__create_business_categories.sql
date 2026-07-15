CREATE TABLE business_categories
(
    id           UUID PRIMARY KEY,

    name         VARCHAR(100) NOT NULL,

    description  TEXT,

    icon_media_id UUID,

    active       BOOLEAN NOT NULL DEFAULT TRUE,

    created_at   TIMESTAMPTZ NOT NULL,

    updated_at   TIMESTAMPTZ NOT NULL,

    deleted_at   TIMESTAMPTZ,

    version      BIGINT NOT NULL,

    CONSTRAINT fk_business_category_icon
        FOREIGN KEY (icon_media_id)
            REFERENCES media(id)
);

ALTER TABLE business_categories
    ADD CONSTRAINT uk_business_categories_name
        UNIQUE (name);

CREATE INDEX idx_business_categories_name
    ON business_categories(name);

CREATE INDEX idx_business_categories_active
    ON business_categories(active);

CREATE INDEX idx_business_categories_deleted_at
    ON business_categories(deleted_at);