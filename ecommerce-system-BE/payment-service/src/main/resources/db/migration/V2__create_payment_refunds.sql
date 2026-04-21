CREATE TABLE payment_refunds
(
    id           UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    payment_id   UUID        NOT NULL,
    amount       BIGINT      NOT NULL,
    status       VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    processed_at TIMESTAMP,
    created_at   TIMESTAMP   NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_payment FOREIGN KEY (payment_id)
        REFERENCES payments (id)
);

CREATE INDEX idx_payment_refunds_payment_id ON payment_refunds (payment_id);
CREATE INDEX idx_payment_refunds_status ON payment_refunds (status);
CREATE INDEX idx_payment_refunds_created_at ON payment_refunds (created_at);