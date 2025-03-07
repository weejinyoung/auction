package com.ourfantasy.auction.auction.service.dto;

import com.ourfantasy.auction.auction.model.Auction;
import com.ourfantasy.auction.auction.model.Bidding;

public record FinalizeSuccessfulBidResponse(
        Long auctionId,
        Long biddingId,
        Long itemId,
        Long hammerPrice
) {
    public static FinalizeSuccessfulBidResponse from(Auction auction, Bidding bidding) {
        return new FinalizeSuccessfulBidResponse(
                auction.getId(),
                bidding.getId(),
                auction.getItem().getId(),
                bidding.getBidPrice()
        );
    }
}
