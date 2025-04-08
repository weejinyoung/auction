package com.ourfantasy.auction.auction.service.dto;

import com.ourfantasy.auction.auction.model.Auction;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record GetAuctionResponse(
        AuctionDescription auction,
        Long likeCount,
        Long followCount
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

    // 1. 기존 방식 (기존 코드 호환)
    public static GetAuctionResponse from(Auction auction) {
        return new GetAuctionResponse(
                new AuctionDescription(
                        new CosignerDescription(
                                auction.getCosigner().getId(),
                                auction.getCosigner().getNickname()
                        ),
                        new ItemDescription(
                                auction.getItem().getId(),
                                auction.getItem().getName(),
                                auction.getItem().getDetail()
                        ),
                        auction.getId(),
                        auction.getStartingPrice(),
                        auction.getHighestBidPrice(),
                        auction.getMinimumBidIncrement(),
                        auction.getClosingAt(),
                        auction.getStatus().getDisplayName()
                ),
                0L, // 기본값: 좋아요 수 0
                0L  // 기본값: 팔로워 수 0
        );
    }

    // 2. 좋아요 + 팔로워 수 포함하는 방식
    public static GetAuctionResponse from(Auction auction, long likeCount, long followerCount) {
        return new GetAuctionResponse(
                new AuctionDescription(
                        new CosignerDescription(
                                auction.getCosigner().getId(),
                                auction.getCosigner().getNickname()
                        ),
                        new ItemDescription(
                                auction.getItem().getId(),
                                auction.getItem().getName(),
                                auction.getItem().getDetail()
                        ),
                        auction.getId(),
                        auction.getStartingPrice(),
                        auction.getHighestBidPrice(),
                        auction.getMinimumBidIncrement(),
                        auction.getClosingAt(),
                        auction.getStatus().getDisplayName()
                ),
                likeCount,
                followerCount
        );
    }
}
