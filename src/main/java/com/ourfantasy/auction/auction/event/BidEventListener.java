package com.ourfantasy.auction.auction.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BidEventListener {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBidPlaced(BidPlacedEvent event) throws InterruptedException {
        Thread.sleep(300);
        log.info("Bid Placed Event: {}", event);
        // TODO 경매 주최자에게 알림
        // TODO 다른 입찰자들에게 알림
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleFinalizeSuccessfulBid(FinalizeSuccessfulBidEvent event) throws InterruptedException {
        Thread.sleep(300);
        log.info("Finalize Successful Bid Event: {}", event);
        // TODO 경매 주최자에게 알림
        // TODO 다른 입찰자들에게 알림
    }
}
