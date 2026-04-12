CREATE TABLE product_variants
(
    id                   UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    product_id           UUID         NOT NULL,
    sku                  VARCHAR(100) NOT NULL UNIQUE,
    price                BIGINT       NOT NULL,
    compare_at_price     BIGINT,
    image_url            VARCHAR(500),
    status               VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by           UUID NULL,
    updated_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by           UUID NULL,
    is_deleted           BOOLEAN      NOT NULL DEFAULT FALSE,
    deleted_at           TIMESTAMP,
    deleted_by           UUID,
    status_change_reason VARCHAR(20),
    version              BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT fk_variant_product
        FOREIGN KEY (product_id)
            REFERENCES m_product (id)
            ON DELETE CASCADE
);