CREATE TABLE m_coupon
(
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code            VARCHAR(255) NOT NULL UNIQUE,
    type            VARCHAR(30)  NOT NULL,
    value           NUMERIC      NOT NULL,
    min_order_value BIGINT       DEFAULT 0,
    max_discount    BIGINT,
    usage_limit     INT,
    used_count      INT          NOT NULL DEFAULT 0,
    start_date      TIMESTAMP    NOT NULL,
    end_date        TIMESTAMP    NOT NULL,
    is_active       BOOLEAN      NOT NULL DEFAULT TRUE,
    description     VARCHAR(255),
    title           VARCHAR(255),

    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP,
    created_by      UUID,
    updated_by      UUID,
    is_deleted      BOOLEAN DEFAULT FALSE,
    deleted_at      TIMESTAMP,
    deleted_by      UUID,
    status_change_reason VARCHAR(50),
    version         BIGINT DEFAULT 0
);

CREATE INDEX idx_m_coupon_active_date ON m_coupon (is_active, start_date, end_date) WHERE is_deleted = FALSE;