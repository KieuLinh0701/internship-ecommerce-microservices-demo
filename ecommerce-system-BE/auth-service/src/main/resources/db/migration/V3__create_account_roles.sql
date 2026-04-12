CREATE TABLE account_roles
(
    account_id           UUID        NOT NULL,
    role_id              UUID        NOT NULL,
    surrogate_id         UUID        NOT NULL DEFAULT gen_random_uuid(),
    status               VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at           TIMESTAMP   NOT NULL DEFAULT NOW(),
    created_by           UUID,
    updated_at           TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_by           UUID,
    is_deleted           BOOLEAN     NOT NULL DEFAULT FALSE,
    deleted_at           TIMESTAMP,
    deleted_by           UUID,
    status_change_reason VARCHAR(20),
    version              BIGINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (account_id, role_id),
    CONSTRAINT uk_account_roles_surrogate_id UNIQUE (surrogate_id),
    CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES accounts (id),
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES m_role (id)
);

CREATE INDEX idx_account_role_account_status ON account_roles (account_id, status);