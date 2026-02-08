package com.example.auction.domain.exception;

public class InvalidEventTypeException extends AuctionException {
    public InvalidEventTypeException(String type) {
        super("Unknown event type: " + type);
    }
}
