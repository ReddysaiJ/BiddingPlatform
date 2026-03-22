CREATE TABLE bid_outbox_events (
    event_id UUID PRIMARY KEY,
    auction_id UUID NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    payload JSONB NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_bid_outbox_status
ON bid_outbox_events(status);