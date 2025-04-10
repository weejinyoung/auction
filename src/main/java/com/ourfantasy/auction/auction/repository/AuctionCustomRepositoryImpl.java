package com.ourfantasy.auction.auction.repository;

import com.ourfantasy.auction.auction.model.*;
import com.ourfantasy.auction.auction.service.dto.AuctionWithCountsProjection;
import com.ourfantasy.auction.item.model.ItemCategory;
import com.ourfantasy.auction.item.model.QItem;
import com.ourfantasy.auction.user.model.QUser;
import com.ourfantasy.auction.user.model.QUserFollow;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
    public Page<AuctionWithCountsProjection> getNearestClosingAuctionsByCategoryWithLikeAndFollow(Pageable pageable, ItemCategory itemCategory) {
        QAuction auction = QAuction.auction;
        QUser user = QUser.user;
        QItem item = QItem.item;
        QBidding bid = QBidding.bidding;
        QAuctionLike auctionLike = QAuctionLike.auctionLike;
        QUserFollow userFollow = QUserFollow.userFollow;

        // 조회 조건: 활성 경매 + 지정된 카테고리
        BooleanExpression conditions = auction.status.eq(AuctionStatus.ACTIVE)
                .and(item.category.eq(itemCategory));


        // 집계 데이터: 입찰 수, 좋아요 수, 팔로워 수
        NumberExpression<Long> bidCount = bid.countDistinct();
        NumberExpression<Long> likeCount = auctionLike.countDistinct();
        NumberExpression<Long> followerCount = userFollow.countDistinct();

        // 메인 쿼리 - 경매와 집계 데이터 함께 조회
        List<Tuple> results = from(auction)
                .select(
                        auction,
                        bidCount,
                        likeCount,
                        followerCount
                )
                .leftJoin(auction.item, item).fetchJoin()
                .leftJoin(auction.cosigner, user).fetchJoin()
                .leftJoin(bid).on(bid.auction.eq(auction))
                .leftJoin(auctionLike).on(auctionLike.auction.eq(auction))
                .leftJoin(userFollow).on(userFollow.followee.eq(user)) // consigner 기준
                .where(conditions)
                .groupBy(auction.id, user.id) // ⚠️ groupBy에 user.id 포함해야 정확한 follower 수 집계
                .orderBy(bidCount.desc(), auction.closingAt.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<AuctionWithCountsProjection> resultDtos = results.stream()
                .map(tuple -> new AuctionWithCountsProjection(
                        tuple.get(auction),
                        Optional.ofNullable(tuple.get(bidCount)).orElse(0L),
                        Optional.ofNullable(tuple.get(likeCount)).orElse(0L),
                        Optional.ofNullable(tuple.get(followerCount)).orElse(0L)
                ))
                .toList();

        // 전체 개수
        Long total = from(auction)
                .leftJoin(auction.item, item)
                .where(conditions)
                .select(auction.countDistinct())
                .fetchOne();

        for (Tuple tuple : results) {
            Auction a = tuple.get(auction);
            Long bidCnt = tuple.get(bidCount);
            Long likeCnt = tuple.get(likeCount);
            Long followCnt = tuple.get(followerCount);

            System.out.printf("📦 Auction ID: %d | 입찰: %d | 좋아요: %d | 팔로워: %d | 출품자: %s\n",
                    a.getId(), bidCnt, likeCnt, followCnt, a.getCosigner().getNickname());
        }

        return new PageImpl<>(resultDtos, pageable, total != null ? total : 0L);
    }

}