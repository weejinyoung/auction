package com.ourfantasy.auction.auction.service.dto;

public record BidRequest(
        Long bidderId,
        Long bidPrice
) {
}
