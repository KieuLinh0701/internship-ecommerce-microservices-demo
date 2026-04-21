CREATE TABLE order_status_history
(
    id       UUID                 DEFAULT gen_random_uuid() PRIMARY KEY,
    order_id UUID        NOT NULL,
    status   VARCHAR(20) NOT NULL,
    time     TIMESTAMP   NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_order_status_history_order
        FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE
);