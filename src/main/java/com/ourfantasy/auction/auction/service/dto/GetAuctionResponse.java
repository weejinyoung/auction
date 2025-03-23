package com.ourfantasy.auction.auction.service.dto;

import com.ourfantasy.auction.auction.model.Auction;
import io.swagger.v3.oas.annotations.media.Schema;

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

    record CosignerDescription(
            Long id,
            String nickname
    ) {
    }

    record AuctionDescription(
            CosignerDescription cosigner,
            ItemDescription item,
            Long auctionId,
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
                        new CosignerDescription(auction.getCosigner().getId(), auction.getCosigner().getNickname()),
                        new ItemDescription(auction.getItem().getId(), auction.getItem().getName(), auction.getItem().getDetail()),
                        auction.getId(),
                        auction.getStartingPrice(),
                        auction.getHighestBidPrice(),
                        auction.getMinimumBidIncrement(),
                        auction.getClosingAt(),
                        auction.getStatus().getDisplayName()
                )
        );
    }
}
