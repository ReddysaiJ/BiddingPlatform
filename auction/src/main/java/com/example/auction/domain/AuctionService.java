package com.example.auction.domain;

import com.example.auction.ApplicationProperties;
import com.example.auction.domain.exception.*;
import com.example.auction.domain.models.*;
import jakarta.validation.Valid;
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
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class AuctionService {
    private static final Logger log = LoggerFactory.getLogger(AuctionService.class);

    private final AuctionRepository auctionRepository;
    private final AuctionOutboxEventRepository auctionOutboxEventRepository;
    private final ApplicationProperties properties;
    private final UserService userService;

    public AuctionService(AuctionRepository auctionRepository, AuctionOutboxEventRepository auctionOutboxEventRepository, ApplicationProperties properties, UserService userService) {
        this.auctionRepository = auctionRepository;
        this.auctionOutboxEventRepository = auctionOutboxEventRepository;
        this.properties = properties;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public PagedResult<AuctionResponse> getAuctions(int pageNo, String sortBy, String direction, String query) {
        return getAuctions(pageNo, sortBy, direction, query, "");
    }

    @Transactional(readOnly = true)
    public PagedResult<AuctionResponse> getAuctions(int pageNo, String sortBy, String direction, String query, String id) {
        Set<String> allowedSort = Set.of("title", "startTime", "endTime", "basePrice", "status");
        if (!allowedSort.contains(sortBy))
            sortBy = "startTime";
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        pageNo = pageNo <= 1 ? 0 : pageNo - 1;
        Pageable pageable = PageRequest.of(pageNo, properties.pageSize(), sort);

        Page<AuctionResponse> auctionEntityPage;
        if(id.isEmpty())
            auctionEntityPage = auctionRepository.findAll(pageable)
                    .map(AuctionMapper::toAuctionDTO);
        else
            auctionEntityPage = auctionRepository.findBySellerId(pageable, id)
                    .map(AuctionMapper::toAuctionDTO);

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
                request.status(),
                request.startTime(),
                request.endTime()
        );
        AuctionEntity saved = auctionRepository.save(auctionEntity);
        log.info("Auction created | uid={} | sellerId={} | name={}", saved.getUid(), seller.id(), seller.name());
        AuctionCreatedEvent payload = AuctionMapper.toAuctionCreatedEvent(saved, seller);
        AuctionEventDTO envelope = AuctionMapper.buildEvent(AuctionEventType.AUCTION_CREATED, saved.getUid(), payload);
        auctionOutboxEventRepository.save(new AuctionOutboxEventEntity(saved.getUid(), envelope.eventType(), envelope));
        return AuctionMapper.toAuctionDTO(saved);
    }

    @Transactional(readOnly = true)
    public AuctionResponse getAuctionResponse(UUID uid) {
        return auctionRepository.findResponseByUid(uid)
                .orElseThrow(() -> new AuctionNotFoundException(
                        "Auction Not Found With UID : " + uid
                ));
    }

    @Transactional(readOnly = true)
    public AuctionDTO getAuctionDTO(UUID uid) {
        return auctionRepository.findDTOByUid(uid)
                .orElseThrow(() -> new AuctionNotFoundException(
                        "Auction Not Found With UID : " + uid
                ));
    }

    @Transactional(readOnly = true)
    AuctionEntity getAuction(UUID uid) {
        return auctionRepository.findByUid(uid)
                .orElseThrow(() -> new AuctionNotFoundException(
                        "Auction Not Found With UID : " + uid
                ));
    }

    @PreAuthorize("hasRole('SELLER')")
    public AuctionResponse update(@Valid UpdateAuctionRequest request) {
        AuctionEntity entity = auctionRepository.findByUid(request.uid())
                .orElseThrow(() -> new AuctionNotFoundException(
                        "Auction Not Found With UID : " + request.uid()
                ));
        Customer currentCustomer = userService.getSeller();
        if (!currentCustomer.id().equals(entity.getSeller().id()))
            throw new AccessDeniedDomainException(currentCustomer.name());

        if (entity.getStatus() == AuctionStatus.CLOSED)
            throw new AuctionClosedModificationException();

        entity.setTitle(request.title());
        entity.setDescription(request.description());
        entity.setBasePrice(request.basePrice());
        entity.setStatus(request.status());
        entity.setStartTime(request.startTime());
        entity.setEndTime(request.endTime());
        entity.setUpdatedAt(LocalDateTime.now());

        AuctionEntity saved = auctionRepository.save(entity);
        log.info("Auction updated | uid={} | sellerId={} | name={}", saved.getUid(), saved.getSeller().id(), saved.getSeller().name());
        AuctionUpdatedEvent payload = AuctionMapper.toAuctionUpdatedEvent(saved, currentCustomer);
        AuctionEventDTO envelope = AuctionMapper.buildEvent(AuctionEventType.AUCTION_UPDATED, saved.getUid(), payload);
        auctionOutboxEventRepository.save(new AuctionOutboxEventEntity(saved.getUid(), envelope.eventType(), envelope));
        return AuctionMapper.toAuctionDTO(saved);
    }

    @PreAuthorize("hasRole('SELLER')")
    public void delete(UUID uid) {
        AuctionEntity entity = auctionRepository.findByUid(uid)
                .orElseThrow(() -> new AuctionNotFoundException(
                        "Auction Not Found With UID : " + uid
                ));

        Customer currentCustomer = userService.getSeller();
        if (!currentCustomer.id().equals(entity.getSeller().id()))
            throw new AccessDeniedDomainException(currentCustomer.name());

        AuctionDeletedEvent payload = new AuctionDeletedEvent(entity.getUid(), currentCustomer.id());
        AuctionEventDTO envelope = AuctionMapper.buildEvent(AuctionEventType.AUCTION_DELETED, entity.getUid(), payload);
        auctionOutboxEventRepository.save(new AuctionOutboxEventEntity(entity.getUid(), envelope.eventType(), envelope));
        auctionRepository.delete(entity);
    }

    @PreAuthorize("hasRole('INTERNAL')")
    public void declareWinner(UUID auctionId, String winner, BigDecimal price) {
        AuctionEntity auction = auctionRepository.findByUid(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException("Auction not found"));

        if (auction.getWinnerId() != null)
            return;

        auctionRepository.markCompleted(auctionId, winner, price);
        AuctionWinnerDeclaredEvent payload = new AuctionWinnerDeclaredEvent(auctionId, winner, price);

        AuctionEventDTO event = AuctionMapper.buildEvent(AuctionEventType.AUCTION_WINNER_DECLARED, auctionId, payload);
        auctionOutboxEventRepository.save(new AuctionOutboxEventEntity(auctionId, event.eventType(), event));
    }
}
