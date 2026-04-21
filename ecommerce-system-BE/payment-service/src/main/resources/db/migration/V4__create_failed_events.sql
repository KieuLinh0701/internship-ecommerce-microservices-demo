CREATE TABLE failed_events
(
    id              UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    topic           VARCHAR(200) NOT NULL,
    event_id        UUID         NOT NULL,
    aggregate_id    UUID         NOT NULL,
    event_type      VARCHAR(100) NOT NULL,
    payload         JSONB        NOT NULL,
    payload_class   VARCHAR(255) NOT NULL,
    error_type      VARCHAR(20)  NOT NULL,
    error_message   TEXT         NOT NULL,
    retry_count     INT          NOT NULL DEFAULT 0,
    max_retry       INT          NOT NULL DEFAULT 3,
    status          VARCHAR(20)  NOT NULL DEFAULT 'FAILED',
    failed_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    last_retry_at   TIMESTAMP,
    resolved_at     TIMESTAMP,
    resolved_by     UUID,
    last_retried_by UUID
);

CREATE INDEX idx_failed_status ON failed_events (status);
CREATE INDEX idx_failed_event_id ON failed_events (event_id);