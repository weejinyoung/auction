package com.ourfantasy.auction.auction.model;

import com.ourfantasy.auction.config.persistence.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "successful_bidding")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SuccessfulBidding extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "bidding_id", nullable = false)
    private Bidding bidding;
    
    @OneToOne
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;

    private SuccessfulBidding(Auction auction, Bidding bidding) {
        this.auction = auction;
        this.bidding = bidding;
    }

    public static SuccessfulBidding createSuccessfulBidding(Auction auction, Bidding bidding) {
        return new SuccessfulBidding(auction, bidding);
    }
}