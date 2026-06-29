package com.example.auction.domain.exception;

public class AuctionOpenModificationException extends AuctionException {
    public AuctionOpenModificationException() {
        super("Live auction cannot be modified");
    }
}
