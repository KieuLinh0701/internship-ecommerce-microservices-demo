CREATE TABLE m_role
(
    id                   UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    name                 VARCHAR(50) NOT NULL UNIQUE,
    status               VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    is_system            BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at           TIMESTAMP   NOT NULL DEFAULT NOW(),
    created_by           UUID,
    updated_at           TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_by           UUID,
    is_deleted           BOOLEAN     NOT NULL DEFAULT FALSE,
    deleted_at           TIMESTAMP,
    deleted_by           UUID,
    status_change_reason VARCHAR(20),
    version              BIGINT      NOT NULL DEFAULT 0
);

CREATE INDEX idx_role_status_delete ON m_role (status, is_deleted);