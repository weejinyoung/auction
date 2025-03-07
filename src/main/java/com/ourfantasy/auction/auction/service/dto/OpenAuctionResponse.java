package com.ourfantasy.auction.auction.service.dto;

import com.ourfantasy.auction.auction.model.Auction;

import java.time.LocalDateTime;

public record OpenAuctionResponse(
        Long auctionId,
        Long cosignerId,
        Long itemId,
        Long startingPrice,
        Long minimumBidIncrement,
        LocalDateTime closingAt
) {
    public static OpenAuctionResponse from(Auction auction) {
        return new OpenAuctionResponse(
                auction.getId(),
                auction.getCosigner().getId(),
                auction.getItem().getId(),
                auction.getStartingPrice(),
                auction.getMinimumBidIncrement(),
                auction.getClosingAt()
        );
    }
}
