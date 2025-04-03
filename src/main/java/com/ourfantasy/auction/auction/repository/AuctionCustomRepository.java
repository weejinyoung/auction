package com.ourfantasy.auction.auction.repository;

import com.ourfantasy.auction.auction.model.Auction;
import com.ourfantasy.auction.item.model.ItemCategory;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;

public interface AuctionCustomRepository {
    Page<Auction> findLatestOpenedAuctions(Pageable pageable);
    Page<Auction> findNearestClosingAuctionsByCategory(Pageable pageable, ItemCategory itemCategory);
    Page<Auction> getNearestClosingAuctionsByCategoryWithLikeAndFollow(Pageable pageable, ItemCategory byDisplayName);
}
