CREATE TABLE business_profiles
(
    id                    UUID PRIMARY KEY,

    user_id               UUID NOT NULL,

    business_category_id  UUID NOT NULL,

    business_name         VARCHAR(150) NOT NULL,

    description           TEXT,

    logo_media_id UUID,

    cover_media_id UUID,

    email                 VARCHAR(255),

    phone                 VARCHAR(30),

    website               VARCHAR(500),

    address               VARCHAR(500),

    county                VARCHAR(100),

    city                  VARCHAR(100),

    country               VARCHAR(100),

    postal_code           VARCHAR(20),

    verified              BOOLEAN NOT NULL DEFAULT FALSE,

    featured              BOOLEAN NOT NULL DEFAULT FALSE,

    accepts_messages      BOOLEAN NOT NULL DEFAULT TRUE,

    created_at            TIMESTAMPTZ NOT NULL,

    updated_at            TIMESTAMPTZ NOT NULL,

    deleted_at            TIMESTAMPTZ,

    version               BIGINT NOT NULL,

    CONSTRAINT fk_business_profile_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_business_profile_category
        FOREIGN KEY (business_category_id)
            REFERENCES business_categories(id),

    CONSTRAINT fk_business_profile_logo
        FOREIGN KEY (logo_media_id)
            REFERENCES media(id),

    CONSTRAINT fk_business_profile_cover
        FOREIGN KEY (cover_media_id)
            REFERENCES media(id)

);

ALTER TABLE business_profiles
    ADD CONSTRAINT uk_business_profile_user
        UNIQUE (user_id);

CREATE INDEX idx_business_profiles_category
    ON business_profiles(business_category_id);

CREATE INDEX idx_business_profiles_verified
    ON business_profiles(verified);

CREATE INDEX idx_business_profiles_featured
    ON business_profiles(featured);

CREATE INDEX idx_business_profiles_business_name
    ON business_profiles(business_name);

CREATE INDEX idx_business_profiles_city
    ON business_profiles(city);

CREATE INDEX idx_business_profiles_county
    ON business_profiles(county);

CREATE INDEX idx_business_profiles_deleted_at
    ON business_profiles(deleted_at);