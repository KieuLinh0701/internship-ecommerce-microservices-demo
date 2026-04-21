CREATE TABLE orders
(
    id                    UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    customer_id           UUID         NOT NULL,
    order_number          VARCHAR(15)  NOT NULL UNIQUE,
    status                VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    subtotal              BIGINT       NOT NULL,
    discount_amount       BIGINT       NOT NULL DEFAULT 0,
    discount_shipping_fee BIGINT       NOT NULL DEFAULT 0,
    shipping_fee          BIGINT       NOT NULL DEFAULT 0,
    total                 BIGINT       NOT NULL,
    payment_method        VARCHAR(20)  NOT NULL,
    payment_status        VARCHAR(20)           DEFAULT 'UNPAID',
    address_id            UUID         NOT NULL,
    receiver_name         VARCHAR(255) NOT NULL,
    receiver_phone        VARCHAR(20)  NOT NULL,
    city_code             INT          NOT NULL,
    city_name             VARCHAR(100) NOT NULL,
    ward_code             INT          NOT NULL,
    ward_name             VARCHAR(100) NOT NULL,
    address_detail        VARCHAR(255) NOT NULL,
    notes                 TEXT,
    cancel_reason         VARCHAR(50),
    cancelled_by          UUID,
    cancel_note           VARCHAR(255),
    cancelled_at          TIMESTAMP,
    cancel_accepted_at    TIMESTAMP,
    cancel_accepted_by    UUID,
    delivered_at          TIMESTAMP,
    completed_at          TIMESTAMP,
    paid_at               TIMESTAMP,
    refund_at             TIMESTAMP,
    returned_at           TIMESTAMP,
    return_reason         VARCHAR(50),
    is_feedback           BOOLEAN               DEFAULT FALSE,
    return_images         TEXT,
    created_at            TIMESTAMP             DEFAULT NOW(),
    created_by            UUID,
    updated_at            TIMESTAMP             DEFAULT NOW(),
    updated_by            UUID,
    is_deleted            BOOLEAN               DEFAULT FALSE,
    deleted_at            TIMESTAMP,
    deleted_by            UUID,
    status_change_reason  VARCHAR(20),
    version               BIGINT                DEFAULT 0
);

CREATE INDEX idx_orders_status ON orders (status) WHERE is_deleted = FALSE;
CREATE INDEX idx_orders_created_at ON orders (created_at DESC) WHERE is_deleted = FALSE;
CREATE INDEX idx_orders_customer_status ON orders (customer_id, status) WHERE is_deleted = FALSE;
CREATE INDEX idx_orders_customer_order_number ON orders (customer_id, order_number) WHERE is_deleted = FALSE;
CREATE INDEX idx_orders_customer_created_at ON orders (customer_id, created_at DESC) WHERE is_deleted = FALSE;