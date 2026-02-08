package com.example.auction.domain.exception;

public class InvalidAuctionTimeException extends AuctionException {
    public InvalidAuctionTimeException() {
        super("Start time must be before end time");
    }
}