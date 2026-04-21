CREATE TABLE order_items
(
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id             UUID         NOT NULL,
    product_id           UUID         NOT NULL,
    variant_id           UUID,
    variant_name         VARCHAR(255),
    variant_image_url    VARCHAR(500),
    product_name         VARCHAR(255) NOT NULL,
    quantity             INT          NOT NULL,
    unit_price           BIGINT       NOT NULL,
    subtotal             BIGINT       NOT NULL,
    discount_amount      BIGINT           DEFAULT 0,
    final_amount         BIGINT       NOT NULL,

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

CREATE INDEX idx_order_items_order_id ON order_items (order_id) WHERE is_deleted = FALSE;
CREATE INDEX idx_order_items_product_id ON order_items (product_id) WHERE is_deleted = FALSE;
CREATE INDEX idx_order_items_variant_id ON order_items (variant_id) WHERE is_deleted = FALSE;