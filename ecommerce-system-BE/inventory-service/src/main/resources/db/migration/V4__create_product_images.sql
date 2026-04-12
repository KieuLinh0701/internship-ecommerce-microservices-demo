CREATE TABLE product_images
(
    id                   UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    product_id           UUID,
    image_url            VARCHAR(500) NOT NULL,
    status               VARCHAR(20)  NOT NULL DEFAULT 'TEMP',
    public_id            VARCHAR(255) NOT NULL,
    sort_order           INTEGER      NOT NULL DEFAULT 0,
    created_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by           UUID NULL,
    updated_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by           UUID NULL,
    is_deleted           BOOLEAN      NOT NULL DEFAULT FALSE,
    deleted_at           TIMESTAMP,
    deleted_by           UUID,
    status_change_reason VARCHAR(20),
    version              BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT fk_product_images_product
        FOREIGN KEY (product_id)
            REFERENCES m_product (id)
            ON DELETE CASCADE
);

CREATE INDEX idx_status_created_at
    ON product_images (status, created_at);
