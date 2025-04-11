package com.ourfantasy.auction.auction.service.dto;

import com.ourfantasy.auction.auction.model.Auction;

import java.time.LocalDateTime;

public record GetAuctionResponseWithLikeAndFollow(
        AuctionDescription auction,
        Long biddingCount,
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
    public static GetAuctionResponseWithLikeAndFollow from(Auction auction, Long biddingCount, long likeCount, long followerCount) {
        return new GetAuctionResponseWithLikeAndFollow(
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
                biddingCount,
                likeCount,
                followerCount
        );
    }
}
