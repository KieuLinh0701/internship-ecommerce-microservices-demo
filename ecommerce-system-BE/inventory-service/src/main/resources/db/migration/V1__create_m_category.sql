CREATE TABLE m_category
(
    id                   UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    name                 VARCHAR(100) NOT NULL,
    parent_id            UUID NULL,
    slug                 VARCHAR(100) NOT NULL UNIQUE,
    image_url            VARCHAR(500),
    status               VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by           UUID NULL,
    updated_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by           UUID NULL,
    is_deleted           BOOLEAN      NOT NULL DEFAULT FALSE,
    deleted_at           TIMESTAMP,
    deleted_by           UUID,
    status_change_reason VARCHAR(20),
    version              BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT uq_category_parent_name UNIQUE (parent_id, name),
    CONSTRAINT fk_category_parent
        FOREIGN KEY (parent_id)
            REFERENCES m_category (id)
            ON DELETE SET NULL
);