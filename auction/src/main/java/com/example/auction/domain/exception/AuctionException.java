package com.example.auction.domain.exception;

public abstract class AuctionException extends RuntimeException {
    protected AuctionException(String message) {
        super(message);
    }
}