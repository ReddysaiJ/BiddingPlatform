CREATE SEQUENCE auction_event_id_seq START WITH 1 INCREMENT BY 50;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE auction_event_outbox (
    id           BIGINT        PRIMARY KEY DEFAULT nextval('auction_event_id_seq'),
    auction_uid  UUID   NOT NULL,
    event_id     UUID          NOT NULL UNIQUE,
    event_type   VARCHAR(50)   NOT NULL,
    payload      JSONB         NOT NULL,
    status       VARCHAR(20)   NOT NULL,
    retry_count  INTEGER       NOT NULL DEFAULT 0,
    created_at   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP
);

CREATE INDEX idx_outbox_status_created
    ON auction_event_outbox (status, created_at);

CREATE INDEX idx_outbox_auction_uid
    ON auction_event_outbox (auction_uid);
