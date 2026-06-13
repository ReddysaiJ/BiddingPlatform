package com.example.bid.domain;

import com.example.bid.domain.models.BidRequest;
import com.example.bid.domain.models.BidResponse;
import com.example.bid.domain.models.BidResponseAuction;

import java.util.UUID;

public interface BidService {
    BidResponse placeBid(BidRequest request);

    PagedResult<BidResponse> getBidsByAuction(UUID auctionId, int pageNo);

    PagedResult<BidResponseAuction> getBidsByUser(String userId, int pageNo);

    BidResponse getHighestBid(UUID auctionId);

    PagedResult<BidResponse> getBidsByUserAndAuction(String userId, UUID auctionId, int pageNo);
}
