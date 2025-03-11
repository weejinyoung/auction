package com.ourfantasy.auction.auction.event;

import com.ourfantasy.auction.auction.model.Auction;
import com.ourfantasy.auction.auction.model.Bidding;

import java.time.LocalDateTime;

public record AcceptBiddingEvent(
        Auction auction,
        Bidding bidding,
        LocalDateTime eventTime
) {
    public AcceptBiddingEvent(Auction auction, Bidding bidding) {
        this(auction, bidding, LocalDateTime.now());
    }
}
