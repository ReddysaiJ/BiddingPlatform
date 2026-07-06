package com.example.chat.job;

import com.example.chat.domain.ChatService;
import com.example.chat.domain.models.AuctionEventDTO;
import com.example.chat.domain.models.AuctionEventType;
import com.example.chat.domain.models.AuctionWinnerDeclaredEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class WinnerEventListener {

    private static final Logger log = LoggerFactory.getLogger(WinnerEventListener.class);

    private final ChatService  chatService;
    private final ObjectMapper objectMapper;

    public WinnerEventListener(ChatService chatService, ObjectMapper objectMapper) {
        this.chatService  = chatService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "#{@chatWinnerQueue.name}")
    public void onWinnerDeclared(AuctionEventDTO event) {
        if (event.eventType() == null) return;

        if (event.eventType() == AuctionEventType.AUCTION_WINNER_DECLARED) {
            AuctionWinnerDeclaredEvent winner = objectMapper.convertValue(event.data(), AuctionWinnerDeclaredEvent.class);

            log.info("AUCTION_WINNER_DECLARED | auctionId={} | winner={} | seller={}",
                    winner.auctionId(), winner.winnerUserId(), winner.sellerUserId());

            chatService.createRoomIfAbsent(
                    winner.auctionId(),
                    winner.sellerUserId(),
                    winner.winnerUserId(),
                    winner.finalPrice()
            );
        } else
            log.debug("Ignoring event type {} in chat-service", event.eventType());
    }
}
