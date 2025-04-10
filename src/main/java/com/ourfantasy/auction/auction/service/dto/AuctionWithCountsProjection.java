package com.ourfantasy.auction.auction.service.dto;

import com.ourfantasy.auction.auction.model.Auction;

public record AuctionWithCountsProjection(
        Auction auction,
        long biddingCount,
        long likeCount,
        long followerCount
) {}
