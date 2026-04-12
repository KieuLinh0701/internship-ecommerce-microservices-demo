CREATE TABLE outbox_events
(
    id             UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    trace_id       VARCHAR(32),
    aggregate_type VARCHAR(100) NOT NULL,
    aggregate_id   UUID         NOT NULL,
    event_type     VARCHAR(100) NOT NULL,
    payload        JSONB        NOT NULL,
    error_message  TEXT,
    status         VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    retry_count    INT          NOT NULL DEFAULT 0,
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    sent_at        TIMESTAMP,
    last_retry_at  TIMESTAMP,
    next_retry_at  TIMESTAMP
);

CREATE INDEX idx_outbox_status_next_retry_created
    ON outbox_events (status, next_retry_at, created_at);