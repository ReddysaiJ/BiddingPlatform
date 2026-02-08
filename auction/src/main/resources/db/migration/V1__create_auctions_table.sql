CREATE SEQUENCE IF NOT EXISTS auction_id_seq START WITH 1 INCREMENT BY 50;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE auctions (
    id            BIGINT PRIMARY KEY DEFAULT nextval('auction_id_seq'),
    uid           UUID NOT NULL UNIQUE,

    title         VARCHAR(255) NOT NULL,
    description   VARCHAR(1000),
    base_price    NUMERIC(12,2) NOT NULL,

    seller_id     VARCHAR(64),
    seller_name   VARCHAR(255),
    seller_email  VARCHAR(255),
    seller_phone  VARCHAR(32),

    status        VARCHAR(32) NOT NULL,

    start_time    TIMESTAMP NOT NULL,
    end_time      TIMESTAMP NOT NULL,

    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP,
    
    winner_id     VARCHAR(100),
    winning_amount NUMERIC(12,2),
    winner_declared BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_auctions_uid ON auctions(uid);
CREATE INDEX idx_auctions_status ON auctions(status);
CREATE INDEX idx_auctions_start_time ON auctions(start_time);
CREATE INDEX idx_auctions_end_time ON auctions(end_time);
CREATE INDEX idx_auctions_winner_declared ON auctions(winner_declared);
