CREATE TABLE carts
(
    id                   UUID PRIMARY KEY   DEFAULT gen_random_uuid(),
    customer_id          UUID      NOT NULL,
    created_at           TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMP NOT NULL DEFAULT NOW(),
    is_deleted           BOOLEAN   NOT NULL DEFAULT FALSE,
    status_change_reason VARCHAR(20),
    version              BIGINT    NOT NULL DEFAULT 0
);

CREATE INDEX idx_cart_customer ON carts (customer_id);