package com.ourfantasy.auction.auction.repository;

import com.ourfantasy.auction.auction.model.Auction;
import com.ourfantasy.auction.auction.model.AuctionStatus;
import com.ourfantasy.auction.auction.model.QAuction;
import com.ourfantasy.auction.item.model.ItemCategory;
import com.ourfantasy.auction.item.model.QItem;
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
        QItem item = QItem.item;

        List<Auction> content = from(auction)
                .distinct()
                .leftJoin(auction.item, item).fetchJoin()
                .where(auction.status.eq(AuctionStatus.ACTIVE))
                .orderBy(auction.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = from(auction)
                .select(auction.countDistinct())
                .where(auction.status.eq(AuctionStatus.ACTIVE))
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public Page<Auction> findNearestClosingAuctionsByCategory(Pageable pageable, ItemCategory itemCategory) {
        QAuction auction = QAuction.auction;
        QItem item = QItem.item;

        List<Auction> content = from(auction)
                .distinct()
                .leftJoin(auction.item, item).fetchJoin()
                .where(auction.status.eq(AuctionStatus.ACTIVE)
                        .and(item.category.eq(itemCategory)))
                .orderBy(auction.closingAt.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = from(auction)
                .join(auction.item, item)
                .select(auction.countDistinct())
                .where(auction.status.eq(AuctionStatus.ACTIVE).and(item.category.eq(itemCategory)))
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }
}