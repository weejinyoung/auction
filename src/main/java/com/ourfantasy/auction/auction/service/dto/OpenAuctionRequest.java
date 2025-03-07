package com.ourfantasy.auction.auction.service.dto;

import java.time.LocalDateTime;

public record OpenAuctionRequest(
        Long cosignerId,
        Long itemId,
        Long startingPrice,
        Long minimumBidIncrement,
        LocalDateTime closingAt
) {
}
