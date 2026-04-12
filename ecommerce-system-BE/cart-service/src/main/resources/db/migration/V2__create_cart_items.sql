CREATE TABLE cart_items
(
    id                   UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    cart_id              UUID        NOT NULL,
    product_id           UUID        NOT NULL,
    variant_id           UUID NULL,
    quantity             INTEGER     NOT NULL CHECK (quantity >= 1),
    status               VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at           TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMP   NOT NULL DEFAULT NOW(),
    is_deleted           BOOLEAN     NOT NULL DEFAULT FALSE,
    status_change_reason VARCHAR(20),
    version              BIGINT      NOT NULL DEFAULT 0,

    CONSTRAINT fk_cart_item_cart FOREIGN KEY (cart_id) REFERENCES carts (id)
);

CREATE INDEX idx_cart_item_cart ON cart_items (cart_id);
CREATE INDEX idx_cart_item_product ON cart_items (product_id);
CREATE INDEX idx_cart_item_variant ON cart_items (variant_id);
CREATE INDEX idx_cart_item_status ON cart_items (status);