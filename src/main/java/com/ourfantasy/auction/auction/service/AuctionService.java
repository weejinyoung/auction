package com.ourfantasy.auction.auction.service;

import com.ourfantasy.auction.auction.event.BidPlacedEvent;
import com.ourfantasy.auction.auction.model.Auction;
import com.ourfantasy.auction.auction.model.Bidding;
import com.ourfantasy.auction.auction.model.SuccessfulBidding;
import com.ourfantasy.auction.auction.repository.AuctionRepository;
import com.ourfantasy.auction.auction.repository.BiddingRepository;
import com.ourfantasy.auction.auction.repository.SuccessfulBiddingRepository;
import com.ourfantasy.auction.auction.service.dto.*;
import com.ourfantasy.auction.config.exception.CustomException;
import com.ourfantasy.auction.config.response.ResponseCode;
import com.ourfantasy.auction.item.model.Item;
import com.ourfantasy.auction.item.repository.ItemRepository;
import com.ourfantasy.auction.user.model.User;
import com.ourfantasy.auction.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final BiddingRepository biddingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final SuccessfulBiddingRepository successfulBiddingRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public OpenAuctionResponse openAuction(OpenAuctionRequest request) {
        User cosigner = userRepository.findById(request.cosignerId())
                .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));
        Item item = itemRepository.findById(request.itemId())
                        .orElseThrow(() -> new CustomException(ResponseCode.ITEM_NOT_FOUND));
        Auction auction = Auction.openAuction(cosigner, item, request.startingPrice(), request.minimumBidIncrement(), request.closingAt());
        auctionRepository.save(auction);
        return OpenAuctionResponse.from(auction);
    }

    @Transactional
    public BidResponse bid(Long auctionId, BidRequest request) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new CustomException(ResponseCode.AUCTION_NOT_FOUND));
        User bidder = userRepository.findById(request.bidderId())
                .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));
        Bidding bidding = auction.placeBid(bidder, request.bidPrice());
        biddingRepository.save(bidding);
        eventPublisher.publishEvent(new BidPlacedEvent(auction, bidding));
        return BidResponse.from(auction, bidding);
    }

    @Transactional
    public FinalizeSuccessfulBidResponse finalizeSuccessfulBid(Long auctionId, FinalizeSuccessfulBidRequest request) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new CustomException(ResponseCode.AUCTION_NOT_FOUND));
        Bidding successfulBid = biddingRepository.findById(request.biddingId())
                .orElseThrow(() -> new CustomException(ResponseCode.BID_NOT_FOUND));
        auction.completeAuction();
        SuccessfulBidding successfulBidding = SuccessfulBidding.createSuccessfulBidding(auction, successfulBid);
        successfulBiddingRepository.save(successfulBidding);
        eventPublisher.publishEvent(new BidPlacedEvent(auction, successfulBid));
        return FinalizeSuccessfulBidResponse.from(auction, successfulBid);
    }
}
