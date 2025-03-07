package com.ourfantasy.auction.auction.repository;

import com.ourfantasy.auction.auction.model.Bidding;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BiddingRepository extends JpaRepository<Bidding, Long> {
}
