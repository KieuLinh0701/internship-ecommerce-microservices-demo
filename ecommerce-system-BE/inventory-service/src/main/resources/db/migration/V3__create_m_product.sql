CREATE TABLE m_product
(
    id                   UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    name                 VARCHAR(255) NOT NULL,
    slug                 VARCHAR(100) NOT NULL UNIQUE,
    description          TEXT,
    category_id          UUID NULL,
    brand_id             UUID NULL,
    status               VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    total_sold           INTEGER               DEFAULT 0,
    thumbnail_url        VARCHAR(500),
    min_price            BIGINT       NOT NULL,
    max_price            BIGINT       NOT NULL,
    created_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by           UUID NULL,
    updated_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by           UUID NULL,
    is_deleted           BOOLEAN      NOT NULL DEFAULT FALSE,
    deleted_at           TIMESTAMP,
    deleted_by           UUID,
    status_change_reason VARCHAR(20),
    version              BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT fk_product_category
        FOREIGN KEY (category_id)
            REFERENCES m_category (id)
            ON DELETE SET NULL,
    CONSTRAINT fk_product_brand
        FOREIGN KEY (brand_id)
            REFERENCES m_brand (id)
            ON DELETE SET NULL
);