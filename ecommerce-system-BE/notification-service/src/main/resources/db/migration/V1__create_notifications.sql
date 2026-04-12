CREATE TABLE notifications
(
    id              UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    account_role_id UUID,
    account_id      UUID,
    type            VARCHAR(50)  NOT NULL,
    channel         VARCHAR(10)  NOT NULL,
    title           VARCHAR(255) NOT NULL,
    body            TEXT         NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    retry_count     INTEGER      NOT NULL DEFAULT 0,
    sent_at         TIMESTAMP,
    read_at         TIMESTAMP,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_notifications_account_role_id ON notifications (account_role_id);
CREATE INDEX idx_notifications_type ON notifications (type);
CREATE INDEX idx_notifications_status ON notifications (status);
CREATE INDEX idx_notifications_created_at ON notifications (created_at);