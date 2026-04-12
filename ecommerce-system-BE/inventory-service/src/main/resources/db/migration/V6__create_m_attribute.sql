CREATE TABLE m_attribute
(
    id                   UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    name                 VARCHAR(50) NOT NULL UNIQUE,
    code                 VARCHAR(20) NOT NULL UNIQUE,
    sort_order           INTEGER     NOT NULL DEFAULT 0,
    status               VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at           TIMESTAMP   NOT NULL DEFAULT NOW(),
    created_by           UUID NULL,
    updated_at           TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_by           UUID NULL,
    is_deleted           BOOLEAN     NOT NULL DEFAULT FALSE,
    deleted_at           TIMESTAMP,
    deleted_by           UUID,
    status_change_reason VARCHAR(20),
    version              BIGINT      NOT NULL DEFAULT 0
);