package com.example.bid.job;

import com.example.bid.domain.models.AuctionEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AuctionEventListener {

    private static final Logger log = LoggerFactory.getLogger(AuctionEventListener.class);

    private final BidAuctionEventHandlerService handler;

    public AuctionEventListener(BidAuctionEventHandlerService handler) {
        this.handler = handler;
    }

    @RabbitListener(
            queues = "${bid.auction.queue:bid.auction.events}",
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void handleAuctionEvent(
            AuctionEventDTO event,
            @Header(value = "x-death", required = false) List<?> deaths
    ) {
        if (deaths != null && !deaths.isEmpty()) {
            long retryCount = deaths.stream()
                    .map(d -> (Map<?, ?>) d)
                    .map(m -> (Number) m.get("count"))
                    .mapToLong(Number::longValue)
                    .sum();

            if (retryCount >= 3) {
                log.error("DLQ | Max retries reached | eventId={}", event.eventId());
                return;
            }
        }

        try {
            handler.handle(event);
        }
        catch (IllegalArgumentException e) {
            log.error("Business error | eventId={}", event.eventId(), e);
            throw new AmqpRejectAndDontRequeueException(e);
        }
        catch (Exception e) {
            log.error("Retrying event | eventId={}", event.eventId(), e);
            throw e;
        }
    }
}
