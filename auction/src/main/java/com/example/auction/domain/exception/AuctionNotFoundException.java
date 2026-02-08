package com.example.auction.domain.exception;

public class AuctionNotFoundException extends RuntimeException{
    public AuctionNotFoundException(String msg){
        super(msg);
    }

    public static AuctionNotFoundException forUID(String uid){
        return new AuctionNotFoundException("Auction with UID " + uid + " not found");
    }
}
