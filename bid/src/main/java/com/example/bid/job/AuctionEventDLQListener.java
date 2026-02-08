package com.example.bid.job;

import com.example.bid.domain.models.AuctionEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class AuctionEventDLQListener {

    private static final Logger log = LoggerFactory.getLogger(AuctionEventDLQListener.class);

    @RabbitListener(queues = "${bid.auction.dlq:bid.auction.events.dlq}")
    public void handleDlq(AuctionEventDTO event,
                          @Header(value = "x-death", required = false) Object xDeath
    ) {
        log.error("DLQ EVENT eventId={} type={} x-death={}", event.eventId(), event.eventType(), xDeath);
    }

}

