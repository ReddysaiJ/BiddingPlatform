package com.example.bid.domain.exception;

public class BidNotAllowedException extends RuntimeException {
    public BidNotAllowedException(String message) {
        super(message);
    }
}

