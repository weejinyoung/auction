package com.ourfantasy.auction.bidding;

import com.ourfantasy.auction.auction.Auction;
import com.ourfantasy.auction.config.BaseTimeEntity;
import com.ourfantasy.auction.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "bidding")
@Getter
@NoArgsConstructor
public class Bidding extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;
    
    @ManyToOne
    @JoinColumn(name = "bidder_id", nullable = false)
    private User bidder;
    
    private String bidPrice;
}