package com.ourfantasy.auction.auction.event;

import com.ourfantasy.auction.auction.model.Auction;
import com.ourfantasy.auction.auction.model.Bidding;

import java.time.LocalDateTime;

public record BidPlacedEvent(
        Auction auction,
        Bidding bidding,
        LocalDateTime eventTime
) {
    public BidPlacedEvent(Auction auction, Bidding bidding) {
        this(auction, bidding, LocalDateTime.now());
    }
}