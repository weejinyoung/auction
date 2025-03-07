package com.ourfantasy.auction.auction.controller;

import com.ourfantasy.auction.auction.service.AuctionService;
import com.ourfantasy.auction.auction.service.dto.BidRequest;
import com.ourfantasy.auction.auction.service.dto.BidResponse;
import com.ourfantasy.auction.auction.service.dto.OpenAuctionRequest;
import com.ourfantasy.auction.auction.service.dto.OpenAuctionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auction", description = "Auction API")
@RestController("/auction")
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;

    @PostMapping
    @Operation(summary = "경매 개시", description = "경매 개시 API 입니다.")
    public OpenAuctionResponse openAuction(OpenAuctionRequest request) {
        return auctionService.openAuction(request);
    }

    @PostMapping("/{auctionId}/bid")
    @Operation(summary = "응찰", description = "응찰 API 입니다.")
    public BidResponse bid(@PathVariable Long auctionId, BidRequest request) {
        return auctionService.bid(auctionId, request);
    }
}
