package com.ourfantasy.auction.auction.service;

import com.ourfantasy.auction.auction.event.BidPlacedEvent;
import com.ourfantasy.auction.auction.event.AcceptBiddingEvent;
import com.ourfantasy.auction.auction.model.Auction;
import com.ourfantasy.auction.auction.model.Bidding;
import com.ourfantasy.auction.auction.model.SuccessfulBidding;
import com.ourfantasy.auction.auction.repository.*;
import com.ourfantasy.auction.auction.service.dto.*;
import com.ourfantasy.auction.config.exception.CustomException;
import com.ourfantasy.auction.config.response.ResponseCode;
import com.ourfantasy.auction.item.model.Item;
import com.ourfantasy.auction.item.model.ItemCategory;
import com.ourfantasy.auction.item.repository.ItemRepository;
import com.ourfantasy.auction.user.model.User;
import com.ourfantasy.auction.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final AuctionCustomRepository auctionCustomRepository;
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
        Auction newAuction = Auction.builderWithValidate()
                .cosigner(cosigner)
                .item(item)
                .startingPrice(request.startingPrice())
                .minimumBidIncrement(request.minimumBidIncrement())
                .closingAt(request.closingAt())
                .build();
        Auction savedAuction = auctionRepository.save(newAuction);
        return OpenAuctionResponse.from(savedAuction);
    }

    @Transactional
    public BidResponse bid(Long auctionId, BidRequest request) {
        Auction auctionToBid = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new CustomException(ResponseCode.AUCTION_NOT_FOUND));
        User bidder = userRepository.findById(request.bidderId())
                .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));
        Bidding newBidding = auctionToBid.bid(bidder, request.bidPrice());
        Bidding savedBidding = biddingRepository.save(newBidding);
        eventPublisher.publishEvent(new BidPlacedEvent(auctionToBid, savedBidding));
        return BidResponse.from(auctionToBid, savedBidding);
    }

    @Transactional
    public AcceptBiddingResponse acceptBidding(Long auctionId, AcceptBiddingRequest request) {
        Auction auctionToComplete = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new CustomException(ResponseCode.AUCTION_NOT_FOUND));
        Bidding biddingToAccept = biddingRepository.findById(request.biddingId())
                .orElseThrow(() -> new CustomException(ResponseCode.BID_NOT_FOUND));
        auctionToComplete.complete();
        SuccessfulBidding successfulBidding = SuccessfulBidding.createSuccessfulBidding(auctionToComplete, biddingToAccept);
        successfulBiddingRepository.save(successfulBidding);
        eventPublisher.publishEvent(new AcceptBiddingEvent(auctionToComplete, biddingToAccept));
        return AcceptBiddingResponse.from(auctionToComplete, biddingToAccept);
    }

    @Transactional(readOnly = true)
    public Page<GetAuctionResponse> getLatestOpenedAuctions(Pageable pageable) {
        return auctionCustomRepository.findLatestOpenedAuctions(pageable)
                .map(GetAuctionResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<GetAuctionResponse> getNearestClosingAuctionsByCategory(Pageable pageable, String itemCategory) {
        return auctionCustomRepository.findNearestClosingAuctionsByCategory(pageable, ItemCategory.findByDisplayName(itemCategory))
                .map(GetAuctionResponse::from);
    }
}
