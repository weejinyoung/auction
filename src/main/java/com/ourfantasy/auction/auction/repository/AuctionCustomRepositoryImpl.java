package com.ourfantasy.auction.auction.repository;

import com.ourfantasy.auction.auction.model.*;
import com.ourfantasy.auction.item.model.ItemCategory;
import com.ourfantasy.auction.item.model.QItem;
import com.ourfantasy.auction.user.model.QUser;
import com.ourfantasy.auction.user.model.QUserFollow;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuctionCustomRepositoryImpl extends QuerydslRepositorySupport implements AuctionCustomRepository {

    public AuctionCustomRepositoryImpl() {
        super(Auction.class);
    }

    @Override
    public Page<Auction> findLatestOpenedAuctions(Pageable pageable) {
        QAuction auction = QAuction.auction;
        QUser user = QUser.user;
        QItem item = QItem.item;

        BooleanExpression conditions = auction.status.eq(AuctionStatus.ACTIVE);

        List<Auction> content = from(auction)
                .leftJoin(auction.item, item).fetchJoin()
                .leftJoin(auction.cosigner, user).fetchJoin()
                .where(conditions)
                .orderBy(auction.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = from(auction)
                .select(auction.countDistinct())
                .where(conditions)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public Page<Auction> findNearestClosingAuctionsByCategory(Pageable pageable, ItemCategory itemCategory) {
        QAuction auction = QAuction.auction;
        QUser user = QUser.user;
        QItem item = QItem.item;

        BooleanExpression conditions = auction.status.eq(AuctionStatus.ACTIVE)
                .and(item.category.eq(itemCategory));

        List<Auction> content = from(auction)
                .leftJoin(auction.item, item).fetchJoin()
                .leftJoin(auction.cosigner, user).fetchJoin()
                .where(conditions)
                .orderBy(auction.closingAt.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = from(auction)
                .join(auction.item, item)
                .select(auction.countDistinct())
                .where(conditions)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }


    @Override
    public Page<Auction> getNearestClosingAuctionsByCategoryWithLikeAndFollow(Pageable pageable, ItemCategory itemCategory) {
        QAuction auction = QAuction.auction;
        QUser user = QUser.user;
        QItem item = QItem.item;
        QBidding bid = QBidding.bidding;
        QAuctionLike auctionLike = QAuctionLike.auctionLike;
        QUserFollow userFollow = QUserFollow.userFollow;

        BooleanExpression conditions = auction.status.eq(AuctionStatus.ACTIVE)
                .and(item.category.eq(itemCategory));

        // 인기 많은 경매 기준: 입찰 수 (Bid 수)
        List<Tuple> results = from(auction)
                .select(
                        auction,
                        bid.count().as("bidCount"),
                        auctionLike.count().as("likeCount"),
                        userFollow.count().as("followerCount")
                )
                .leftJoin(auction.item, item).fetchJoin()
                .leftJoin(auction.cosigner, user).fetchJoin()
                .leftJoin(bid).on(bid.auction.eq(auction))
                .leftJoin(auctionLike).on(auctionLike.auction.eq(auction))
                .leftJoin(userFollow).on(userFollow.followee.eq(user))
                .where(conditions)
                .groupBy(auction.id)
                .orderBy(bid.count().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Auction> auctions = results.stream()
                .map(tuple -> tuple.get(auction))
                .toList();

        // 전체 개수 (집계는 Bid 기준으로 처리)
        Long total = from(auction)
                .leftJoin(auction.item, item)
                .where(conditions)
                .select(auction.countDistinct())
                .fetchOne();

        return new PageImpl<>(auctions, pageable, total != null ? total : 0L);
    }
}