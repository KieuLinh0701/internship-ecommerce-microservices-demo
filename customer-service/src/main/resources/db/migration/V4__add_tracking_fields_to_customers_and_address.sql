ALTER TABLE customers
    ADD COLUMN created_by UUID,
    ADD COLUMN updated_by UUID,
    ADD COLUMN is_delete BOOLEAN DEFAULT FALSE,
    ADD COLUMN version BIGINT DEFAULT 0;

ALTER TABLE addresses
    ADD COLUMN created_by UUID,
    ADD COLUMN updated_by UUID,
    ADD COLUMN is_delete BOOLEAN DEFAULT FALSE,
    ADD COLUMN version BIGINT DEFAULT 0;
