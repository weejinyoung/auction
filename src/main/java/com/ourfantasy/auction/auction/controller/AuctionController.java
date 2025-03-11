package com.ourfantasy.auction.auction.controller;

import com.ourfantasy.auction.auction.service.AuctionService;
import com.ourfantasy.auction.auction.service.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auction", description = "Auction API")
@RestController("/api/v1/auction")
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;

    @GetMapping("/recent")
    @Operation(summary = "가장 최근에 열린 경매 리스트 조회", description = "가장 최근에 열린 경매 리스트 조회 API 입니다.")
    public Page<GetAuctionResponse> getLatestOpenedAuctions(@PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return auctionService.getLatestOpenedAuctions(pageable);
    }

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
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 