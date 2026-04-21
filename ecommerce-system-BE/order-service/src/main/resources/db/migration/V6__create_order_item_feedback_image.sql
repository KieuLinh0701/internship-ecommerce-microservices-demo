CREATE TABLE order_item_feedback_image
(
    id          UUID                  DEFAULT gen_random_uuid() PRIMARY KEY,
    feedback_id UUID         NOT NULL,
    image_url   VARCHAR(500) NOT NULL,
    status      VARCHAR(20)  NOT NULL DEFAULT 'TEMP',
    public_id   VARCHAR(255) NOT NULL,
    sort_order  INTEGER      NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by  UUID,
    is_deleted  BOOLEAN      NOT NULL DEFAULT FALSE,
    version     BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT fk_feedback_image_feedback
        FOREIGN KEY (feedback_id) REFERENCES order_item_feedback (id)
);

CREATE INDEX idx_feedback_image_feedback_id ON order_item_feedback_image (feedback_id);