CREATE TABLE m_attribute_value
(
    id                   UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    attribute_id         UUID        NOT NULL,
    value                VARCHAR(50) NOT NULL,
    code                 VARCHAR(20) NOT NULL,
    sort_order           INTEGER     NOT NULL DEFAULT 0,
    status               VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    description          VARCHAR(100),
    created_at           TIMESTAMP   NOT NULL DEFAULT NOW(),
    created_by           UUID NULL,
    updated_at           TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_by           UUID NULL,
    is_deleted           BOOLEAN     NOT NULL DEFAULT FALSE,
    deleted_at           TIMESTAMP,
    deleted_by           UUID,
    status_change_reason VARCHAR(20),
    version              BIGINT      NOT NULL DEFAULT 0,

    CONSTRAINT fk_attribute_value_attribute
        FOREIGN KEY (attribute_id)
            REFERENCES m_attribute (id)
            ON DELETE CASCADE
);