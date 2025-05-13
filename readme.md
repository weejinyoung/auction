### 낙관락 적용 응찰 코드

```kotlin
    @Transactional
    public BidResponse bid(Long auctionId, BidRequest request) {
        // 1. 경매와 입찰자 정보 조회
        Auction auctionToBid = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new CustomException(ResponseCode.AUCTION_NOT_FOUND));
        User bidder = userRepository.findById(request.bidderId())
                .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

        // 2. 입찰 전 현재 최고 입찰가 저장
        Long currentHighestBid = auctionToBid.getHighestBidPrice();

        // 3. 입찰 유효성 검증 (비즈니스 로직)
        auctionToBid.validateBid(bidder, request.bidPrice());

        // 4. 낙관적 락을 이용한 업데이트 시도
        int updatedRows = auctionRepository.updateHighestBidPriceWithOptimisticLock(
                request.bidPrice(),
                auctionId,
                currentHighestBid  // 현재 값과 일치할 때만 업데이트
        );

        // 5. 업데이트 실패 시 (다른 사용자가 이미 변경함) 예외 발생
        if (updatedRows == 0) {
            throw new CustomException(ResponseCode.BID_CONFLICT);
        }

        // 6. 업데이트 성공 시 엔티티 상태 동기화
        auctionToBid.setHighestBidPrice(request.bidPrice());

        // 7. 입찰 내역 생성 및 저장
        Bidding newBidding = new Bidding(auctionToBid, bidder, request.bidPrice());
        Bidding savedBidding = biddingRepository.save(newBidding);

        // 8. 이벤트 발행
        eventPublisher.publishEvent(new BidPlacedEvent(auctionToBid, savedBidding));

        return BidResponse.from(auctionToBid, savedBidding);
    }

```