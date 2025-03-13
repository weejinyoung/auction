package com.ourfantasy.auction.auction.repository;

import com.ourfantasy.auction.auction.model.Auction;
import com.ourfantasy.auction.item.model.ItemCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuctionCustomRepository {
    Page<Auction> findLatestOpenedAuctions(Pageable pageable);
    Page<Auction> findNearestClosingAuctionsByCategory(Pageable pageable, ItemCategory itemCategory);
}
