package com.ourfantasy.auction.auction;

import com.ourfantasy.auction.config.BaseTimeEntity;
import com.ourfantasy.auction.item.Item;
import com.ourfantasy.auction.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "auction")
@Getter
@NoArgsConstructor
public class Auction extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cosigner_id", nullable = false)
    private User cosigner;
    
    @OneToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
    
    private Long startingPrice;
    
    @Enumerated(EnumType.STRING)
    private AuctionStatus status;
    
    private Long bidIncrement;
}