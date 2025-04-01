package com.ourfantasy.auction.auction.service.dto;

import com.ourfantasy.auction.auction.model.Auction;
import com.ourfantasy.auction.auction.model.QAuction;
import com.ourfantasy.auction.rating.model.QItemRating;
import com.ourfantasy.auction.rating.model.QUserRating;
import com.querydsl.core.Tuple;

import java.time.LocalDateTime;

public record GetAuctionResponseWithRating(
        AuctionDescription auction
) {
    record RatingDescription(
            Double userRating,
            Double itemRating
    ) {
    }

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
            RatingDescription rating,
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
    public static GetAuctionResponseWithRating fromTuple(Tuple tuple) {
        QAuction auctionAlias = QAuction.auction;
        QItemRating itemRating = QItemRating.itemRating;
        QUserRating userRating = QUserRating.userRating;

        Auction auction = tuple.get(auctionAlias);
        Double itemScore = tuple.get(itemRating.score.avg());
        Double userScore = tuple.get(userRating.score.avg());

        return new GetAuctionResponseWithRating(
                new AuctionDescription(
                        new RatingDescription(
                                itemScore,
                                userScore
                        ),
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
                )
        );
    }
}
