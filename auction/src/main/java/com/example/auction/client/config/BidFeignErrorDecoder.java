//package com.example.auction.client.config;
//
//import com.example.auction.domain.AuctionNotFoundException;
//import feign.Response;
//import feign.codec.ErrorDecoder;
//import org.springframework.stereotype.Component;
//
//@Component
//public class BidFeignErrorDecoder implements ErrorDecoder {
//
//    private final ErrorDecoder defaultDecoder = new Default();
//
//    @Override
//    public Exception decode(String methodKey, Response response) {
//        return switch (response.status()) {
//            case 404 -> new IllegalArgumentException("Auction not found in Bid service");
//            case 500 -> new IllegalStateException("Bid service internal error");
//            default -> defaultDecoder.decode(methodKey, response);
//        };
//    }
//}