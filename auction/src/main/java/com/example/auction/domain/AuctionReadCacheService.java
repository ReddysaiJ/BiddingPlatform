package com.example.auction.domain;

import com.example.auction.domain.exception.AuctionNotFoundException;
import com.example.auction.domain.models.AuctionCacheResponse;
import com.example.auction.domain.models.AuctionStatus;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class AuctionReadCacheService {
    private static final Logger log =  LoggerFactory.getLogger(AuctionReadCacheService.class);

    private final AuctionRepository auctionRepository;

    public AuctionReadCacheService(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    @Cacheable(
            value = "openAuctions",
            key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort",
            sync = true
    )
    public PagedResult<AuctionCacheResponse> getOpenAuctions(Pageable pageable) {
        log.info("Opening Auctions for page: {}", pageable.getPageNumber());

        Page<AuctionCacheResponse> page = auctionRepository.findAllByStatus(AuctionStatus.OPEN, pageable);
        return new PagedResult<>(
            page.getContent(),
            page.getTotalElements(),
            page.getNumber() + 1,
            page.getTotalPages(),
            page.isFirst(),
            page.isLast(),
            page.hasNext(),
            page.hasPrevious()
        );
    }

    @Cacheable(
            value = "draftAuctions",
            key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort",
            sync = true
    )
    public PagedResult<AuctionCacheResponse> getDraftAuctions(Pageable pageable) {
        log.info("Draft Auctions for page: {}", pageable.getPageNumber());

        Page<AuctionCacheResponse> page = auctionRepository.findAllByStatus(AuctionStatus.DRAFT, pageable);
        return new PagedResult<>(
                page.getContent(),
                page.getTotalElements(),
                page.getNumber() + 1,
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious()
        );
    }

    @Cacheable(
            value = "closedAuctions",
            key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort",
            sync = true
    )
    public PagedResult<AuctionCacheResponse> getClosedAuctions(Pageable pageable) {
        log.info("Closed Auctions for page: {}", pageable.getPageNumber());

        Page<AuctionCacheResponse> page = auctionRepository.findAllByStatus(AuctionStatus.CLOSED, pageable);
        return new PagedResult<>(
                page.getContent(),
                page.getTotalElements(),
                page.getNumber() + 1,
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious()
        );
    }

    @Cacheable(
            value = "allAuctions",
            key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort",
            sync = true
    )
    public PagedResult<AuctionCacheResponse> getAllAuctions(Pageable pageable) {
        Page<AuctionCacheResponse> page = auctionRepository.findAllCached(pageable);
        return new PagedResult<>(
                page.getContent(),
                page.getTotalElements(),
                page.getNumber() + 1,
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious()
        );
    }

    @Cacheable(
            value = "auctionByUid",
            key = "#uid",
            sync = true
    )
    public AuctionCacheResponse getAuctionByUid(UUID uid) {
        return auctionRepository.findCacheResponseByUid(uid)
                .orElseThrow(() -> new AuctionNotFoundException("Auction Not Found With UID : " + uid));
    }

    public PagedResult<AuctionCacheResponse> getAllAuctionsByQuery(String query, Pageable pageable) {
        Page<AuctionCacheResponse> page = auctionRepository.findByTitleContainingIgnoreCaseAll(query, pageable);
        return new PagedResult<>(
                page.getContent(),
                page.getTotalElements(),
                page.getNumber() + 1,
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious()
        );
    }

    public PagedResult<AuctionCacheResponse> getAllMyAuctions(String query, @NotBlank(message = "Customer ID is required") String id, Pageable pageable) {
        Page<AuctionCacheResponse> page = query.isBlank()
                ? auctionRepository.findBySellerId(id, pageable)
                : auctionRepository.findBySellerIdAndTitleContainingIgnoreCase(id, query, pageable);
        return new PagedResult<>(
                page.getContent(),
                page.getTotalElements(),
                page.getNumber() + 1,
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious()
        );
    }

    @Caching(evict = {
            @CacheEvict(value = "openAuctions", allEntries = true),
            @CacheEvict(value = "allAuctions", allEntries = true)
    })
    public void evictOpenCache() {}

    @Caching(evict = {
            @CacheEvict(value = "draftAuctions", allEntries = true),
            @CacheEvict(value = "allAuctions", allEntries = true)
    })
    public void evictDraftCache() {}

    @CacheEvict(value = "closedAuctions", allEntries = true)
    public void evictClosedCache() {}

    @CacheEvict(value = "auctionByUid", key = "#uid")
    public void evictAuctionByUid(UUID uid) {}
}
