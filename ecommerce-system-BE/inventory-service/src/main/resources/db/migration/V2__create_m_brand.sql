CREATE TABLE m_brand
(
    id                   UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    name                 VARCHAR(100) NOT NULL,
    slug                 VARCHAR(100) NOT NULL UNIQUE,
    logo_url             VARCHAR(500),
    status               VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    description          TEXT,
    created_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by           UUID NULL,
    updated_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by           UUID NULL,
    is_deleted           BOOLEAN      NOT NULL DEFAULT FALSE,
    deleted_at           TIMESTAMP,
    deleted_by           UUID,
    status_change_reason VARCHAR(20),
    version              BIGINT       NOT NULL DEFAULT 0
);