package com.ourfantasy.auction.auction.service.dto;

import com.ourfantasy.auction.auction.model.Auction;

import java.time.LocalDateTime;

public record GetAuctionResponse(
        ItemDescription item,
        AuctionDescription auction
) {

    record ItemDescription(
            Long id,
            String name,
            String detail
    ) {
    }

    record AuctionDescription(
            Long id,
            Long cosignerId,
            Long startingPrice,
            Long highestBidPrice,
            Long minimumBidIncrement,
            LocalDateTime closingAt,
            String status
    ) {
    }

    public static GetAuctionResponse from(Auction auction) {
        return new GetAuctionResponse(
                new ItemDescription(
                        auction.getItem().getId(),
                        auction.getItem().getName(),
                        auction.getItem().getDetail()
                ),
                new AuctionDescription(
                        auction.getId(),
                        auction.getCosigner().getId(),
                        auction.getStartingPrice(),
                        auction.getHighestBidPrice(),
                        auction.getMinimumBidIncrement(),
                        auction.getClosingAt(),
                        auction.getStatus().name()
                )
        );
    }
}
