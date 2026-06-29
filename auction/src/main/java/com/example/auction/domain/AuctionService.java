package com.example.auction.domain;

import com.example.auction.ApplicationProperties;
import com.example.auction.domain.exception.*;
import com.example.auction.domain.models.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class AuctionService {
    private static final Logger log = LoggerFactory.getLogger(AuctionService.class);

    private final AuctionRepository auctionRepository;
    private final AuctionOutboxEventRepository auctionOutboxEventRepository;
    private final ApplicationProperties properties;
    private final UserService userService;
    private final CloudinaryService cloudinaryService;
    private final AuctionWatchlistService watchlistService;
    private final AuctionReadCacheService auctionReadCacheService;

    public AuctionService(
            AuctionRepository auctionRepository,
            AuctionOutboxEventRepository auctionOutboxEventRepository,
            ApplicationProperties properties,
            UserService userService, CloudinaryService cloudinaryService,
            AuctionWatchlistService watchlistService, AuctionReadCacheService auctionReadCacheService) {
        this.auctionRepository = auctionRepository;
        this.auctionOutboxEventRepository = auctionOutboxEventRepository;
        this.properties = properties;
        this.userService = userService;
        this.cloudinaryService = cloudinaryService;
        this.watchlistService = watchlistService;
        this.auctionReadCacheService = auctionReadCacheService;
    }


    @Transactional(readOnly = true)
    public PagedResult<AuctionResponse> getAuctions(
            int pageNo, String sortBy, String direction, AuctionStatus status) {

        String userId = userService.getSeller().id();
        Pageable pageable = buildPageable(pageNo, sortBy, direction);

        PagedResult<AuctionCacheResponse> page = switch (status){
            case OPEN -> auctionReadCacheService.getOpenAuctions(pageable);
            case CLOSED -> auctionReadCacheService.getClosedAuctions(pageable);
            case DRAFT -> auctionReadCacheService.getDraftAuctions(pageable);
            case COMPLETED -> null;
        };

        return toPagedResult(page, userId);
    }

    @Transactional(readOnly = true)
    public PagedResult<AuctionResponse> getAllAuctions(
            int pageNo, String sortBy, String direction) {

        String userId = userService.getSeller().id();
        Pageable pageable = buildPageable(pageNo, sortBy, direction);

        PagedResult<AuctionCacheResponse> page = auctionReadCacheService.getAllAuctions(pageable);

        return toPagedResult(page, userId);
    }

    @Transactional(readOnly = true)
    public AuctionResponse getAuctionResponse(UUID uid) {
        String userId = userService.getSeller().id();

        AuctionCacheResponse cached = auctionReadCacheService.getAuctionByUid(uid);

        boolean watched = watchlistService.isWatched(userId, uid);
        return toResponse(cached, watched);
    }

    @Transactional(readOnly = true)
    public AuctionImageResponse getAuctionImages(UUID uid) {
        AuctionEntity entity = auctionRepository.findByUid(uid)
                .orElseThrow(() -> new AuctionNotFoundException(
                        "Auction Not Found With UID : " + uid));
        for(String s : entity.getImageUrls())
            System.out.println(s);
        return new AuctionImageResponse(
                uid,
                entity.getBaseImageUrl(),
                new ArrayList<>(entity.getImageUrls())
        );
    }

    @Transactional(readOnly = true)
    public PagedResult<AuctionResponse> getAllAuctionsByQuery(int pageNo, String sortBy, String direction, String query) {
        String userId = userService.getSeller().id();
        Pageable pageable = buildPageable(pageNo, sortBy, direction);
        PagedResult<AuctionCacheResponse> page = auctionReadCacheService.getAllAuctionsByQuery(query, pageable);
        return toPagedResult(page, userId);
    }

    @Transactional(readOnly = true)
    public PagedResult<AuctionResponse> getAllMyAuctions(int pageNo, String sortBy, String direction, String query, @NotBlank(message = "Customer ID is required") String id) {
        Pageable pageable = buildPageable(pageNo, sortBy, direction);
        PagedResult<AuctionCacheResponse> page = auctionReadCacheService.getAllMyAuctions(query, id, pageable);
        return toPagedResult(page, id);
    }


    @Transactional
    @PreAuthorize("hasRole('SELLER')")
    public AuctionResponse create(CreateAuctionRequest request) {
        if (!request.startTime().isBefore(request.endTime()))
            throw new InvalidAuctionTimeException();

        Customer seller = userService.getSeller();

        AuctionEntity auctionEntity = new AuctionEntity(
                request.title(),
                request.description(),
                request.basePrice(),
                seller,
                AuctionStatus.DRAFT,
                request.startTime(),
                request.endTime()
        );

        if (request.baseImageUrl() != null && !request.baseImageUrl().isBlank()) {
            auctionEntity.setBaseImageUrl(request.baseImageUrl());
            auctionEntity.setBasePublicId(request.basePublicId());
        }

        AuctionEntity saved = auctionRepository.save(auctionEntity);
        log.info("Auction created | uid={} | sellerId={} | name={}",
                saved.getUid(), seller.id(), seller.name());

        publishEvent(AuctionEventType.AUCTION_CREATED,
                saved.getUid(), AuctionMapper.toAuctionCreatedEvent(saved, seller));

        auctionReadCacheService.evictDraftCache();
        return AuctionMapper.toAuctionDTO(saved, false);
    }

    @Transactional
    @PreAuthorize("hasRole('SELLER')")
    public void saveImageUrls(UUID uid, List<String> imageUrls, List<String> publicIds) {
        AuctionEntity entity = auctionRepository.findByUid(uid)
                .orElseThrow(() -> new AuctionNotFoundException(
                        "Auction Not Found With UID : " + uid));

        Customer currentCustomer = userService.getSeller();
        if (!currentCustomer.id().equals(entity.getSeller().id()))
            throw new AccessDeniedDomainException(currentCustomer.name());

        entity.getImageUrls().addAll(imageUrls);
        entity.getImagePublicIds().addAll(publicIds);
        auctionRepository.save(entity);

        auctionReadCacheService.evictAuctionByUid(uid);
    }

    @Transactional
    @PreAuthorize("hasRole('SELLER')")
    public AuctionResponse update(@Valid UpdateAuctionRequest request) {
        AuctionEntity entity = auctionRepository.findByUid(request.uid())
                .orElseThrow(() -> new AuctionNotFoundException(
                        "Auction Not Found With UID : " + request.uid()));

        Customer currentCustomer = userService.getSeller();
        if (!currentCustomer.id().equals(entity.getSeller().id()))
            throw new AccessDeniedDomainException(currentCustomer.name());

        if (entity.getStatus() == AuctionStatus.CLOSED)
            throw new AuctionClosedModificationException();

        entity.setTitle(request.title());
        entity.setDescription(request.description());
        entity.setBasePrice(request.basePrice());
        entity.setStatus(AuctionStatus.DRAFT);
        entity.setStartTime(request.startTime());
        entity.setEndTime(request.endTime());
        entity.setUpdatedAt(LocalDateTime.now());

        AuctionEntity saved = auctionRepository.save(entity);
        log.info("Auction updated | uid={} | sellerId={} | name={}",
                saved.getUid(), saved.getSeller().id(), saved.getSeller().name());

        publishEvent(AuctionEventType.AUCTION_UPDATED,
                saved.getUid(), AuctionMapper.toAuctionUpdatedEvent(saved, currentCustomer));

        auctionReadCacheService.evictAuctionByUid(saved.getUid());
        auctionReadCacheService.evictDraftCache();

        return AuctionMapper.toAuctionDTO(saved, false);
    }

    @Transactional
    @PreAuthorize("hasRole('SELLER')")
    public void delete(UUID uid) {
        AuctionEntity entity = auctionRepository.findByUid(uid)
                .orElseThrow(() -> new AuctionNotFoundException(
                        "Auction Not Found With UID : " + uid));

        Customer currentCustomer = userService.getSeller();
        if (!currentCustomer.id().equals(entity.getSeller().id()))
            throw new AccessDeniedDomainException(currentCustomer.name());

        if(!entity.getStatus().equals(AuctionStatus.DRAFT))
            throw new AuctionOpenModificationException();

        if (!entity.getImagePublicIds().isEmpty())
            cloudinaryService.deleteImages(entity.getImagePublicIds());

        publishEvent(AuctionEventType.AUCTION_DELETED,
                entity.getUid(), new AuctionDeletedEvent(entity.getUid(), currentCustomer.id()));

        auctionReadCacheService.evictAuctionByUid(uid);
        auctionReadCacheService.evictDraftCache();

        auctionRepository.delete(entity);
    }

    @Transactional
    @PreAuthorize("hasRole('INTERNAL')")
    public void declareWinner(UUID auctionId, String winner, BigDecimal price) {
        AuctionEntity auction = auctionRepository.findByUid(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException("Auction not found"));

        if (auction.getWinnerId() != null)
            return;

        auctionRepository.markCompleted(auctionId, winner, price);
        publishEvent(AuctionEventType.AUCTION_WINNER_DECLARED,
                auctionId, new AuctionWinnerDeclaredEvent(auctionId, winner, price));
    }

    private PagedResult<AuctionResponse> toPagedResult(PagedResult<AuctionCacheResponse> cacheResponse, String userId) {
        Set<UUID> watchedUids = watchlistService.getWatchedUids(userId);
        List<AuctionResponse> content = cacheResponse.data().stream()
                .map(dto -> toResponse(dto, watchedUids.contains(dto.uid())))
                .toList();
        return new PagedResult<>(
                content,
                cacheResponse.totalElements(),
                cacheResponse.pageNumber(),
                cacheResponse.totalPages(),
                cacheResponse.isFirst(),
                cacheResponse.isLast(),
                cacheResponse.hasNext(),
                cacheResponse.hasPrevious()
        );
    }

    private AuctionResponse toResponse(AuctionCacheResponse dto, boolean watched) {
        return new AuctionResponse(
                dto.uid(),
                dto.title(),
                dto.description(),
                dto.basePrice(),
                dto.seller(),
                dto.status(),
                dto.baseImageUrl(),
                dto.startTime(),
                dto.endTime(),
                watched,
                dto.createdAt(),
                dto.updatedAt()
        );
    }

    private Pageable buildPageable(int pageNo, String sortBy, String direction) {
        Set<String> allowed = Set.of("title", "startTime", "endTime", "basePrice", "status");
        if (!allowed.contains(sortBy))
            sortBy = "startTime";
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        return PageRequest.of(pageNo <= 1 ? 0 : pageNo - 1, properties.pageSize(), sort);
    }


    private void publishEvent(AuctionEventType type, UUID auctionId, Object payload) {
        AuctionEventDTO envelope = AuctionMapper.buildEvent(type, auctionId, payload);
        auctionOutboxEventRepository.save(
                new AuctionOutboxEventEntity(auctionId, envelope.eventType(), envelope));
    }


}