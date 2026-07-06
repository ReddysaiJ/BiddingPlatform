CREATE SEQUENCE IF NOT EXISTS chat_room_id_seq START 1 INCREMENT 50;

CREATE TABLE IF NOT EXISTS chat_rooms (
    id          BIGINT      PRIMARY KEY DEFAULT nextval('chat_room_id_seq'),
    auction_id  UUID        NOT NULL UNIQUE,
    seller_id   TEXT        NOT NULL,
    winner_id   TEXT        NOT NULL,
    final_price NUMERIC(19,2) NOT NULL,
    created_at  TIMESTAMP   NOT NULL
);



CREATE SEQUENCE IF NOT EXISTS chat_msg_id_seq START 1 INCREMENT 50;

CREATE TABLE IF NOT EXISTS chat_messages (
    id          BIGINT      PRIMARY KEY DEFAULT nextval('chat_msg_id_seq'),
    room_id     BIGINT      NOT NULL REFERENCES chat_rooms(id) ON DELETE CASCADE,
    sender_id   TEXT        NOT NULL,
    sender_name TEXT        NOT NULL,
    content     VARCHAR(2000) NOT NULL,
    sent_at     TIMESTAMP   NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_chat_messages_room ON chat_messages (room_id, sent_at);
