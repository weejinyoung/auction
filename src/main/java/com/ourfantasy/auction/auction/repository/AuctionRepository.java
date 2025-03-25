package com.ourfantasy.auction.auction.repository;

import com.ourfantasy.auction.auction.model.Auction;
import com.ourfantasy.auction.auction.model.AuctionStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

    Page<Auction> findByStatusOrderByCreatedAtDesc(AuctionStatus status, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Auction a SET a.highestBidPrice = :newBidPrice WHERE a.highestBidPrice = :currentBidPrice AND a.id = :auctionId")
    int updateHighestBidPriceWithOptimisticLock(
            @Param("newBidPrice") Long newBidPrice,
            @Param("currentBidPrice") Long currentBidPrice,
            @Param("auctionId") Long auctionId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Auction a WHERE a.id = :auctionId")
    Optional<Auction> findAuctionForUpdate(@Param("auctionId") Long auctionId);
}
