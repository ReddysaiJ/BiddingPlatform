CREATE TABLE processed_event_entity (
    event_id UUID PRIMARY KEY,
    processed_at TIMESTAMP NOT NULL,
    status VARCHAR(32) NOT NULL
);

CREATE INDEX idx_processed_event_status
    ON processed_event_entity(status);
CREATE INDEX idx_processed_event_processed_at
    ON processed_event_entity(processed_at);