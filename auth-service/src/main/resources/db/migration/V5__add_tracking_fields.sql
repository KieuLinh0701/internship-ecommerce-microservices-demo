ALTER TABLE accounts
    ADD COLUMN created_by UUID,
    ADD COLUMN updated_by UUID,
    ADD COLUMN is_delete BOOLEAN DEFAULT FALSE,
    ADD COLUMN version BIGINT DEFAULT 0;

ALTER TABLE roles
    ADD COLUMN created_by UUID,
    ADD COLUMN updated_by UUID,
    ADD COLUMN is_delete BOOLEAN DEFAULT FALSE,
    ADD COLUMN version BIGINT DEFAULT 0;

ALTER TABLE account_roles
    ADD COLUMN created_by UUID,
    ADD COLUMN updated_by UUID,
    ADD COLUMN is_delete BOOLEAN DEFAULT FALSE,
    ADD COLUMN version BIGINT DEFAULT 0;