CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE auction_watchlist (
    id BIGSERIAL    PRIMARY KEY,

    user_id         VARCHAR(50) NOT NULL,
    auction_id      BIGINT NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_watchlist_auction
        FOREIGN KEY (auction_id)
        REFERENCES auctions(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_user_auction UNIQUE (user_id, auction_id)
);
