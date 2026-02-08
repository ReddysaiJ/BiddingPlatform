package com.example.bid.clients.config;

import com.example.bid.domain.exception.AuctionNotFoundException;
import com.example.bid.domain.exception.BidNotAllowedException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

@Component
public class AuctionFeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 404 -> new AuctionNotFoundException(
                    "Auction not found in Auction service"
            );
            case 403 -> new BidNotAllowedException(
                    "Bidding not allowed on this auction"
            );
            case 400 -> new IllegalArgumentException(
                    "Invalid request sent to Auction service"
            );
            default -> defaultDecoder.decode(methodKey, response);
        };
    }
}
