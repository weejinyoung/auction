package com.ourfantasy.auction.auction.service.dto;

import com.ourfantasy.auction.auction.model.Auction;

import java.time.LocalDateTime;

public record GetAuctionResponse(
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
            ItemDescription item,
            Long startingPrice,
            Long highestBidPrice,
            Long minimumBidIncrement,
            LocalDateTime closingAt,
            String status
    ) {
    }

    public static GetAuctionResponse from(Auction auction) {
        return new GetAuctionResponse(
                new AuctionDescription(
                        auction.getId(),
                        auction.getCosigner().getId(),
                        new ItemDescription(
                                auction.getItem().getId(),
                                auction.getItem().getName(),
                                auction.getItem().getDetail()
                        ),
                        auction.getStartingPrice(),
                        auction.getHighestBidPrice(),
                        auction.getMinimumBidIncrement(),
                        auction.getClosingAt(),
                        auction.getStatus().getDisplayName()
                )
        );
    }
}
