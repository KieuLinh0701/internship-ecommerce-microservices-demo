CREATE TABLE customers
(
    id                   UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    account_id           UUID         NOT NULL,
    full_name            VARCHAR(255) NOT NULL,
    phone                VARCHAR(11),
    avatar_url           VARCHAR(500),
    created_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by           UUID,
    updated_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by           UUID,
    is_deleted           BOOLEAN      NOT NULL DEFAULT FALSE,
    deleted_at           TIMESTAMP,
    deleted_by           UUID,
    status_change_reason VARCHAR(20),
    version              BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX idx_customers_account_deleted
    ON customers (account_id, is_deleted);

CREATE UNIQUE INDEX uq_customers_account
    ON customers (account_id) WHERE is_deleted = false;