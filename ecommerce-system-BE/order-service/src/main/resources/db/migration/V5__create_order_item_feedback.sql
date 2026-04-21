CREATE TABLE order_item_feedback
(
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_item_id        UUID         NOT NULL,
    customer_id          UUID         NOT NULL,
    product_id           UUID         NOT NULL,
    product_slug         VARCHAR(255) NOT NULL,
    product_name         VARCHAR(255) NOT NULL,
    variant_image_url    VARCHAR(500),
    rating               SMALLINT     NOT NULL,
    comment              TEXT,
    is_anonymous         BOOLEAN          DEFAULT FALSE,
    status               VARCHAR(20)      DEFAULT 'PENDING',
    approved_at          TIMESTAMP,
    approved_by          TIMESTAMP,
    is_updated           BOOLEAN          DEFAULT FALSE,

    created_at           TIMESTAMP        DEFAULT NOW(),
    updated_at           TIMESTAMP,
    created_by           UUID,
    updated_by           UUID,
    is_deleted           BOOLEAN          DEFAULT FALSE,
    deleted_at           TIMESTAMP,
    deleted_by           UUID,
    status_change_reason VARCHAR(20),
    version               BIGINT                DEFAULT 0
);