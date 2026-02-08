package com.example.auction.domain.exception;

public class AccessDeniedDomainException extends AuctionException {
    public AccessDeniedDomainException(String msg) {
        super(msg);
    }
}
