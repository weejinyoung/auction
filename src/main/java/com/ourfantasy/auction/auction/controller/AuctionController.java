package com.ourfantasy.auction.auction.controller;

import com.ourfantasy.auction.auction.service.AuctionService;
import com.ourfantasy.auction.auction.service.dto.*;
import com.ourfantasy.auction.item.model.ItemCategory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/nearest-closing/{itemCategory}")
    @Operation(summary = "종료 시간이 임박한 경매 리스트 조회", description = "종료 시간이 임박한 경매 리스트 조회 API 입니다, 특정 카테고리의 경매를 필터링할 수 있습니다.")
    public Page<GetAuctionResponse> getNearestClosingAuctionsByCategory(
            @PageableDefault(size = 10, sort = "closingAt") Pageable pageable,
            @PathVariable String itemCategory
    ) {
        return auctionService.getNearestClosingAuctionsByCategory(pageable, itemCategory);
    }

    @PostMapping
    @Operation(summary = "경매 개시", description = "경매 개시 API 입니다.")
    public OpenAuctionResponse openAuction(@RequestBody OpenAuctionRequest request) {
        return auctionService.openAuction(request);
    }

    @PostMapping("/{auctionId}/bid")
    @Operation(summary = "응찰", description = "응찰 API 입니다.")
    public BidResponse bid(@PathVariable Long auctionId, @RequestBody BidRequest request) {
        return auctionService.bid(auctionId, request);
    }
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 