package com.example.bid.domain;

import com.example.bid.domain.models.BidRequest;
import com.example.bid.domain.models.BidResponse;

import java.util.UUID;

public interface BidService {
    BidResponse placeBid(BidRequest request);

    PagedResult<BidResponse> getBidsByAuction(UUID auctionId, int pageNo);

    PagedResult<BidResponse> getBidsByUser(String userId, int pageNo);

    BidResponse getHighestBid(UUID auctionId);
}
