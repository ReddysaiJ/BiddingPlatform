CREATE TABLE open_auctions (
   auction_uid UUID PRIMARY KEY,
   user_id VARCHAR(100) NOT NULL,
   highest_bid NUMERIC(12,2),
   highest_bidder_id VARCHAR(100),
   bid_count INT NOT NULL DEFAULT 0
);

CREATE INDEX idx_open_auctions_user
    ON open_auctions(user_id);
CREATE INDEX idx_open_auctions_highest_bid
    ON open_auctions(highest_bid DESC);