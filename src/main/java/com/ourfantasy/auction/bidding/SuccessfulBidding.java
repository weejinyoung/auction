package com.ourfantasy.auction.bidding;

import com.ourfantasy.auction.auction.Auction;
import com.ourfantasy.auction.config.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "successful_bidding")
@Getter
@NoArgsConstructor
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
}