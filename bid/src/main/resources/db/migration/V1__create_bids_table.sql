CREATE SEQUENCE IF NOT EXISTS bid_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE bids (
      id BIGINT PRIMARY KEY DEFAULT nextval('bid_id_seq'),
      auction_id UUID NOT NULL,
      auction_title VARCHAR(100) NOT NULL,
      user_id VARCHAR(100) NOT NULL,
      amount NUMERIC(12,2) NOT NULL,
      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
      winner BOOLEAN NOT NULL DEFAULT FALSE,
      final_price NUMERIC(12,2)
);

CREATE INDEX idx_bids_auction
    ON bids (auction_id);
CREATE INDEX idx_bids_user
    ON bids (user_id);
CREATE INDEX idx_bids_auction_amount
    ON bids (auction_id, amount DESC);
CREATE INDEX idx_bids_auction_winner
    ON bids (auction_id, winner);
CREATE INDEX idx_bids_auction_final_price
    ON bids (auction_id, final_price DESC);