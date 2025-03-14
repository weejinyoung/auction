package com.ourfantasy.auction.auction.model;

import com.ourfantasy.auction.config.exception.CustomException;
import com.ourfantasy.auction.config.persistence.BaseTimeEntity;
import com.ourfantasy.auction.config.response.ResponseCode;
import com.ourfantasy.auction.item.model.Item;
import com.ourfantasy.auction.user.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "auction")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column(nullable = false)
    private Long startingPrice;

    @Column(nullable = false)
    private Long highestBidPrice;

    @Column(nullable = false)
    private Long minimumBidIncrement;

    @Column(nullable = false)
    private LocalDateTime closingAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionStatus status;

    @Builder(builderMethodName = "builderWithValidate")
    private Auction(User cosigner, Item item, Long startingPrice, Long minimumBidIncrement, LocalDateTime closingAt) {
        validateAuctionCreating(cosigner, item, startingPrice, minimumBidIncrement, closingAt);
        this.cosigner = cosigner;
        this.item = item;
        this.startingPrice = startingPrice;
        this.highestBidPrice = startingPrice;
        this.minimumBidIncrement = minimumBidIncrement;
        this.closingAt = closingAt;
        this.status = AuctionStatus.ACTIVE;
    }

    private void validateAuctionCreating(User cosigner, Item item, Long startingPrice, Long minimumBidIncrement, LocalDateTime closingTime) {
        if (cosigner.isInactive()) {
            throw new CustomException(ResponseCode.AUCTION_INACTIVE_USER);
        }
        if(!item.getOwner().getId().equals(cosigner.getId())) {
            throw new CustomException(ResponseCode.ITEM_NOT_OWNED);
        }
        if (startingPrice < 0) {
            throw new CustomException(ResponseCode.INVALID_STARTING_PRICE);
        }
        if (minimumBidIncrement < 0) {
            throw new CustomException(ResponseCode.INVALID_MINIMUM_BID_INCREMENT);
        }
        if (closingTime.isBefore(LocalDateTime.now())) {
            throw new CustomException(ResponseCode.CLOSING_TIME_INVALID);
        }
    }

    public Long getMinimumBidPrice() {
        return this.highestBidPrice + this.minimumBidIncrement;
    }

    public boolean isInactive() {
        return this.status != AuctionStatus.ACTIVE || this.closingAt.isBefore(LocalDateTime.now());
    }

    public Bidding bid(User bidder, Long bidPrice) {
        validateBid(bidder, bidPrice);
        this.highestBidPrice = bidPrice;
        return new Bidding(this, bidder, bidPrice);
    }

    private void validateBid(User bidder, Long bidPrice) {
        if (isInactive()) {
            throw new CustomException(ResponseCode.AUCTION_NOT_ACTIVE);
        }
        if (bidder.isInactive()) {
            throw new CustomException(ResponseCode.INACTIVE_BIDDER);
        }
        if (getMinimumBidPrice() > bidPrice) {
            throw new CustomException(ResponseCode.BID_INCREMENT_VIOLATION);
        }
        if (bidder.getId().equals(this.cosigner.getId())) {
            throw new CustomException(ResponseCode.BID_OWNER_CONFLICT);
        }
    }

    public void complete() {
        this.status = AuctionStatus.COMPLETED;
    }
}