package com.ourfantasy.auction.auction.repository;

import com.ourfantasy.auction.auction.model.Auction;
import com.ourfantasy.auction.auction.model.Bidding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BiddingRepository extends JpaRepository<Bidding, Long> {

    List<Bidding> findBiddingsByAuctionOrderByCreatedAtAsc(Auction auction);
}
