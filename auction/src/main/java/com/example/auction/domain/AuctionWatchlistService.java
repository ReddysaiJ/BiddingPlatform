package com.example.auction.domain;

import com.example.auction.ApplicationProperties;
import com.example.auction.domain.models.AuctionResponse;
import com.example.auction.domain.models.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@PreAuthorize("isAuthenticated()")
public class AuctionWatchlistService {
    private static final Logger log = LoggerFactory.getLogger(AuctionWatchlistService.class);

    private final AuctionWatchlistRepository auctionWatchlistRepository;
    private final ApplicationProperties properties;
    private final AuctionService auctionService;
    private final UserService userService;

    public AuctionWatchlistService(AuctionWatchlistRepository auctionWatchlistRepository, ApplicationProperties properties, AuctionService auctionService, UserService userService) {
        this.auctionWatchlistRepository = auctionWatchlistRepository;
        this.properties = properties;
        this.auctionService = auctionService;
        this.userService = userService;
    }

    public Customer removeFromWatchlist(UUID uid) {
        log.info("Deleting auction from watchlist");
        Customer user = userService.getSeller();
        AuctionEntity auctionEntity = auctionService.getAuction(uid);
        auctionWatchlistRepository.findByUserIdAndAuction_Id(user.id(), auctionEntity.getId())
                .ifPresent(auctionWatchlistRepository::delete);
        return user;
    }

    public Customer addToWatchlist(UUID uid) {
        log.info("Adding auction to watchlist");
        Customer user = userService.getSeller();
        AuctionEntity auctionEntity = auctionService.getAuction(uid);
        if (auctionWatchlistRepository.existsByUserIdAndAuction_Uid(user.id(), uid))
            return user;

        auctionWatchlistRepository.save(new WatchlistEntity(user.id(), auctionEntity));
        return user;
    }

    public PagedResult<AuctionResponse> getAuctions(int pageNo, String sortBy, String direction) {
        log.info("Fetching watchlist auctions");
        Set<String> allowedSort = Set.of("title", "startTime", "endTime", "basePrice", "status");
        if (!allowedSort.contains(sortBy))
            sortBy = "startTime";

        String sortField = switch (sortBy) {
            case "title" -> "auction.title";
            case "endTime" -> "auction.endTime";
            case "basePrice" -> "auction.basePrice";
            case "status" -> "auction.status";
            case "createdAt" -> "auction.createdAt";
            default -> "auction.startTime";
        };
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortField).descending()
                : Sort.by(sortField).ascending();
        pageNo = pageNo <= 1 ? 0 : pageNo - 1;
        Pageable pageable = PageRequest.of(pageNo, properties.pageSize(), sort);

        Customer user = userService.getSeller();
        Page<AuctionResponse> auctionEntityPage = auctionWatchlistRepository.findWatchedAuctions(pageable, user.id());

        return new PagedResult<>(
                auctionEntityPage.getContent(),
                auctionEntityPage.getTotalElements(),
                auctionEntityPage.getNumber() + 1,
                auctionEntityPage.getTotalPages(),
                auctionEntityPage.isFirst(),
                auctionEntityPage.isLast(),
                auctionEntityPage.hasNext(),
                auctionEntityPage.hasPrevious()
        );
    }
}
