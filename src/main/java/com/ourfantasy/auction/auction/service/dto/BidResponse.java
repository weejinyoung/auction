package com.ourfantasy.auction.auction.service.dto;

import com.ourfantasy.auction.auction.model.Auction;
import com.ourfantasy.auction.auction.model.Bidding;

public record BidResponse(
        Long auctionId,
        Long itemId,
        Long bidId,
        Long bidderId,
        Long bidPrice
) {
    public static BidResponse from(Auction auction, Bidding bidding) {
        return new BidResponse(
                auction.getId(),
                auction.getItem().getId(),
                bidding.getId(),
                bidding.getBidder().getId(),
                bidding.getBidPrice()
        );
    }
}
