CREATE TABLE customer_addresses
(
    id                   UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    customer_id          UUID         NOT NULL,
    name                 VARCHAR(255) NOT NULL,
    phone                VARCHAR(20)  NOT NULL,
    city_code            INT          NOT NULL,
    city_name            VARCHAR(100) NOT NULL,
    ward_code            INT          NOT NULL,
    ward_name            VARCHAR(100) NOT NULL,
    detail               VARCHAR(255) NOT NULL,
    is_default           BOOLEAN               DEFAULT FALSE,
    created_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by           UUID,
    updated_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by           UUID,
    is_deleted           BOOLEAN      NOT NULL DEFAULT FALSE,
    deleted_at           TIMESTAMP,
    deleted_by           UUID,
    status_change_reason VARCHAR(20),
    version              BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES customers (id)
);

CREATE INDEX idx_addresses_customer_deleted
    ON customer_addresses (customer_id, is_deleted);

CREATE INDEX idx_addresses_customer_default
    ON customer_addresses (customer_id, is_deleted, is_default DESC);

CREATE INDEX idx_addresses_customer_created
    ON customer_addresses (customer_id, is_deleted, created_at);

CREATE INDEX idx_addresses_id_customer_deleted
    ON customer_addresses (id, customer_id, is_deleted);