package com.ourfantasy.auction.auction.repository;

import com.ourfantasy.auction.auction.model.SuccessfulBidding;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuccessfulBiddingRepository extends JpaRepository<SuccessfulBidding, Long> {
}
