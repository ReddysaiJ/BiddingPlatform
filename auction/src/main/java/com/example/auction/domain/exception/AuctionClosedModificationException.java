package com.example.auction.domain.exception;

public class AuctionClosedModificationException extends AuctionException {
    public AuctionClosedModificationException() {
        super("Closed auction cannot be modified");
    }
}
