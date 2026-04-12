CREATE TABLE accounts
(
    id                    UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    email                 VARCHAR(255) NOT NULL UNIQUE,
    password              VARCHAR(255) NOT NULL,
    status                VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    failed_login_attempts INT          NOT NULL DEFAULT 0,
    locked_until          TIMESTAMP NULL,
    lock_reason           VARCHAR(20)  NOT NULL DEFAULT 'NONE',
    last_login_at         TIMESTAMP,
    created_at            TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by            UUID,
    updated_at            TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by            UUID,
    is_deleted            BOOLEAN      NOT NULL DEFAULT FALSE,
    deleted_at            TIMESTAMP,
    deleted_by            UUID,
    status_change_reason  VARCHAR(20),
    version               BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX idx_account_email_delete ON accounts (email, is_deleted);