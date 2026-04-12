CREATE TABLE product_variant_inventory
(
    id                   UUID PRIMARY KEY   DEFAULT gen_random_uuid(),
    variant_id           UUID      NOT NULL UNIQUE,
    quantity             INTEGER   NOT NULL DEFAULT 0,
    reserved_quantity    INTEGER   NOT NULL DEFAULT 0,
    low_stock_threshold  INTEGER   NOT NULL DEFAULT 10,
    manufacture_date     DATE,
    expiry_date          DATE,
    sold_quantity        INTEGER   NOT NULL DEFAULT 0,
    status               VARCHAR(20),
    created_at           TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by           UUID NULL,
    updated_at           TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_by           UUID NULL,
    version              BIGINT    NOT NULL DEFAULT 0,

    CONSTRAINT fk_inventory_variant
        FOREIGN KEY (variant_id)
            REFERENCES product_variants (id)
            ON DELETE RESTRICT
);