CREATE TABLE product_variant_attribute_value
(
    id                   UUID PRIMARY KEY   DEFAULT gen_random_uuid(),
    variant_id           UUID      NOT NULL,
    attribute_value_id   UUID      NOT NULL,
    created_at           TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by           UUID NULL,
    updated_at           TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_by           UUID NULL,
    is_deleted           BOOLEAN   NOT NULL DEFAULT FALSE,
    deleted_at           TIMESTAMP,
    deleted_by           UUID,
    status_change_reason VARCHAR(20),
    version              BIGINT    NOT NULL DEFAULT 0,

    CONSTRAINT fk_pvav_variant
        FOREIGN KEY (variant_id)
            REFERENCES product_variants (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_pvav_attribute_value
        FOREIGN KEY (attribute_value_id)
            REFERENCES m_attribute_value (id)
            ON DELETE CASCADE,
    CONSTRAINT uq_variant_attribute UNIQUE (variant_id, attribute_value_id)
);