package com.example.bid.domain;

import com.example.bid.ApplicationProperties;
import com.example.bid.domain.exception.BidNotAllowedException;
import com.example.bid.domain.exception.InvalidBidException;
import com.example.bid.domain.models.*;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
class BidServiceImpl implements BidService {

    private final BidRepository bidRepository;
    private final ApplicationProperties properties;
    private final OpenAuctionsRepository openAuctionsRepository;
    private final BidOutboxRepository bidOutboxRepository;

    public BidServiceImpl(BidRepository bidRepository, ApplicationProperties properties, UserService userService,
                          OpenAuctionsRepository openAuctionsRepository, BidOutboxRepository bidOutboxRepository) {
        this.bidRepository = bidRepository;
        this.properties = properties;
        this.openAuctionsRepository = openAuctionsRepository;
        this.bidOutboxRepository = bidOutboxRepository;
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('BUYER')")
    public BidResponse placeBid(BidRequest request) {
        validateBidRequest(request);
        String currentUser = UserService.getCurrentUserId();

        if (!currentUser.equals(request.userId()))
            throw new BidNotAllowedException("User mismatch");

        OpenAuctionsEntity auction = openAuctionsRepository.findByIdForUpdate(request.auctionId())
                .orElseThrow(() -> new BidNotAllowedException("Auction not open"));

        if (auction.getUserId().equals(request.userId()))
            throw new BidNotAllowedException("Owner cannot bid");

        BigDecimal highestBid = auction.getHighestBid();
        if (highestBid != null && request.amount().compareTo(highestBid) <= 0)
            throw new InvalidBidException("Bid must be higher than current highest bid");

        BidEntity bid = BidMapper.toBidEntity(request);
        BidEntity saved = bidRepository.saveAndFlush(bid);

        auction.setHighestBid(saved.getAmount());
        auction.setHighestBidderId(saved.getUserId());
        auction.setBidCount(auction.getBidCount() == null ? 1 : auction.getBidCount() + 1);

        BidOutboxEventEntity outbox = new BidOutboxEventEntity(
                request.auctionId(),
                AuctionEventType.HIGHEST_BID_UPDATED,
                BidMapper.toHighestBidDTO(saved),
                OutboxStatus.NEW
        );

        bidOutboxRepository.save(outbox);
        return BidMapper.toBidResponse(saved);
    }


    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('SELLER')")
    public PagedResult<BidResponse> getBidsByAuction(UUID auctionId, int pageNo) {
        if (auctionId == null)
            throw new InvalidBidException("auctionId must not be null");

        Pageable pageable = createPageable(pageNo);
        Page<BidResponse> page = bidRepository.findByAuctionIdOrderByCreatedAtDesc(pageable, auctionId);
        return BidMapper.toPagedResult(page);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("#userId == authentication.name or hasRole('ADMIN')")
    public PagedResult<BidResponse> getBidsByUser(String userId, int pageNo) {
        if (!StringUtils.hasText(userId))
            throw new IllegalArgumentException("userId must not be blank");

        Pageable pageable = createPageable(pageNo);
        Page<BidResponse> page = bidRepository.findByUserIdOrderByCreatedAtDesc(pageable, userId);
        return BidMapper.toPagedResult(page);
    }

    @Override
    public BidResponse getHighestBid(UUID auctionId) {
        if (auctionId == null)
            throw new IllegalArgumentException("auctionId must not be null");

        Pageable topOne = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "amount"));
        BidEntity highestBidEntity = bidRepository.findHighestBid(topOne, auctionId)
                .stream()
                .findFirst()
                .orElse(null);

        if (highestBidEntity == null)
            return null;

        return BidMapper.toBidResponse(highestBidEntity);
    }

    private Pageable createPageable(int pageNo) {
        int safePage = Math.max(pageNo - 1, 0);
        int size = Math.max(properties.pageSize(), 1);
        return PageRequest.of(safePage, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    private void validateBidRequest(BidRequest request) {
        if (request == null)
            throw new IllegalArgumentException("BidRequest must not be null");

        if (request.auctionId() == null)
            throw new IllegalArgumentException("auctionId must not be null");

        if (!StringUtils.hasText(request.userId()))
            throw new IllegalArgumentException("userId must not be blank");

        if (request.amount() == null || request.amount().signum() <= 0)
            throw new IllegalArgumentException("bid amount must be greater than zero");
    }
}
