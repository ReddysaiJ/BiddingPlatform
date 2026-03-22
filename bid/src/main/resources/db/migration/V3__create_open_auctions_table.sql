CREATE TABLE open_auctions (
    auction_uid UUID PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL,
    highest_bid NUMERIC,
    highest_bidder_id VARCHAR(100),
    bid_count INT DEFAULT 0
);