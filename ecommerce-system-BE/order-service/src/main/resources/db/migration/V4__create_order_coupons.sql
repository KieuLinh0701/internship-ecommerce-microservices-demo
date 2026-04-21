CREATE TABLE order_coupons
(
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id             UUID   NOT NULL,
    coupon_id            UUID   NOT NULL,
    discount_applied     BIGINT NOT NULL,

    created_at           TIMESTAMP        DEFAULT NOW(),
    updated_at           TIMESTAMP,
    created_by           UUID,
    updated_by           UUID,
    is_deleted           BOOLEAN          DEFAULT FALSE,
    deleted_at           TIMESTAMP,
    deleted_by           UUID,
    status_change_reason VARCHAR(20),
    version              BIGINT           DEFAULT 0
);

CREATE INDEX idx_order_coupons_order ON order_coupons (order_id);