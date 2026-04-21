CREATE TABLE payments
(
    id               UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    customer_id       UUID NOT NULL,
    order_id         UUID        NOT NULL,
    method           VARCHAR(20) NOT NULL,
    amount           BIGINT      NOT NULL,
    status           VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    transaction_id   VARCHAR(100) UNIQUE,
    idempotency_key  VARCHAR(100) UNIQUE,
    gateway_response TEXT,
    created_at       TIMESTAMP   NOT NULL DEFAULT NOW(),
    completed_at     TIMESTAMP
);

CREATE INDEX idx_payments_status ON payments (status);
CREATE INDEX idx_payments_order_id ON payments (order_id);
CREATE INDEX idx_payments_created_at ON payments (created_at);