package com.ourfantasy.auction.auction.model;

import com.ourfantasy.auction.config.exception.CustomException;
import com.ourfantasy.auction.config.response.ResponseCode;
import com.ourfantasy.auction.item.model.Item;
import com.ourfantasy.auction.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuctionModelTest {

    @Mock
    private User activeUser;

    @Mock
    private User inactiveUser;

    @Mock
    private User anotherActiveUser;

    @Mock
    private Item item;

    private Auction auction;
    private LocalDateTime validClosingTime;

    @BeforeEach
    void setUp() {
        validClosingTime = LocalDateTime.now().plusDays(7);

        // 필요한 경우에만 스터빙 설정 - 실제 사용되는 메서드만 스터빙
        lenient().when(activeUser.getId()).thenReturn(1L);
        lenient().when(activeUser.isInactive()).thenReturn(false);

        lenient().when(inactiveUser.getId()).thenReturn(2L);
        lenient().when(inactiveUser.isInactive()).thenReturn(true);

        lenient().when(anotherActiveUser.getId()).thenReturn(3L);
        lenient().when(anotherActiveUser.isInactive()).thenReturn(false);

        lenient().when(item.getOwner()).thenReturn(activeUser);

        // 테스트용 경매 객체 생성
        auction = createTestAuction();
    }

    private Auction createTestAuction() {
        return Auction.builderWithValidate()
                .cosigner(activeUser)
                .item(item)
                .startingPrice(1000L)
                .minimumBidIncrement(50L)
                .closingAt(validClosingTime)
                .build();
    }

    @Nested
    @DisplayName("경매 생성 테스트")
    class AuctionCreationTests {

        @Test
        @DisplayName("유효한 정보로 경매 생성 성공")
        void shouldCreateAuctionWithValidInformation() {
            assertThat(auction).isNotNull();
            assertThat(auction.getCosigner()).isEqualTo(activeUser);
            assertThat(auction.getItem()).isEqualTo(item);
            assertThat(auction.getStartingPrice()).isEqualTo(1000L);
            assertThat(auction.getHighestBidPrice()).isEqualTo(1000L);
            assertThat(auction.getMinimumBidIncrement()).isEqualTo(50L);
            assertThat(auction.getClosingAt()).isEqualTo(validClosingTime);
            assertThat(auction.getStatus()).isEqualTo(AuctionStatus.ACTIVE);
        }

        @Test
        @DisplayName("비활성 사용자는 경매 생성 불가")
        void shouldThrowExceptionWhenCosignerIsInactive() {
            // 이 테스트에서 실제로 사용되는 스터빙
            when(inactiveUser.isInactive()).thenReturn(true);

            assertThatThrownBy(() ->
                    Auction.builderWithValidate()
                            .cosigner(inactiveUser)
                            .item(item)
                            .startingPrice(1000L)
                            .minimumBidIncrement(50L)
                            .closingAt(validClosingTime)
                            .build()
            )
                    .isInstanceOf(CustomException.class)
                    .extracting("responseCode")
                    .isEqualTo(ResponseCode.AUCTION_INACTIVE_USER);
        }

        @Test
        @DisplayName("아이템 소유자만 경매 생성 가능")
        void shouldThrowExceptionWhenUserIsNotItemOwner() {
            // 이 테스트에서 실제로 사용되는 스터빙
            User nonOwner = mock(User.class);
            when(nonOwner.getId()).thenReturn(999L);
            when(nonOwner.isInactive()).thenReturn(false);
            when(item.getOwner()).thenReturn(anotherActiveUser);

            assertThatThrownBy(() ->
                    Auction.builderWithValidate()
                            .cosigner(nonOwner)
                            .item(item)
                            .startingPrice(1000L)
                            .minimumBidIncrement(50L)
                            .closingAt(validClosingTime)
                            .build()
            )
                    .isInstanceOf(CustomException.class)
                    .extracting("responseCode")
                    .isEqualTo(ResponseCode.ITEM_NOT_OWNED);

            // 테스트 후 원래 상태로 복원
            when(item.getOwner()).thenReturn(activeUser);
        }

        @Test
        @DisplayName("시작가는 음수일 수 없음")
        void shouldThrowExceptionWhenStartingPriceIsNegative() {
            assertThatThrownBy(() ->
                    Auction.builderWithValidate()
                            .cosigner(activeUser)
                            .item(item)
                            .startingPrice(-100L)
                            .minimumBidIncrement(50L)
                            .closingAt(validClosingTime)
                            .build()
            )
                    .isInstanceOf(CustomException.class)
                    .extracting("responseCode")
                    .isEqualTo(ResponseCode.INVALID_STARTING_PRICE);
        }

        @Test
        @DisplayName("최소 입찰 증가액은 음수일 수 없음")
        void shouldThrowExceptionWhenBidIncrementIsNegative() {
            assertThatThrownBy(() ->
                    Auction.builderWithValidate()
                            .cosigner(activeUser)
                            .item(item)
                            .startingPrice(1000L)
                            .minimumBidIncrement(-10L)
                            .closingAt(validClosingTime)
                            .build()
            )
                    .isInstanceOf(CustomException.class)
                    .extracting("responseCode")
                    .isEqualTo(ResponseCode.INVALID_MINIMUM_BID_INCREMENT);
        }

        @Test
        @DisplayName("마감 시간은 현재보다 미래여야 함")
        void shouldThrowExceptionWhenClosingTimeIsInPast() {
            LocalDateTime pastTime = LocalDateTime.now().minusDays(1);

            assertThatThrownBy(() ->
                    Auction.builderWithValidate()
                            .cosigner(activeUser)
                            .item(item)
                            .startingPrice(1000L)
                            .minimumBidIncrement(50L)
                            .closingAt(pastTime)
                            .build()
            )
                    .isInstanceOf(CustomException.class)
                    .extracting("responseCode")
                    .isEqualTo(ResponseCode.CLOSING_TIME_INVALID);
        }
    }

    @Nested
    @DisplayName("입찰 테스트")
    class BiddingTests {

        @Test
        @DisplayName("유효한 조건으로 입찰 성공")
        void shouldPlaceBidWithValidConditions() {
            // 이 테스트에서 실제 사용되는 스터빙
            when(anotherActiveUser.getId()).thenReturn(3L);
            when(anotherActiveUser.isInactive()).thenReturn(false);

            Bidding bidding = auction.bid(anotherActiveUser, 1100L);

            assertThat(bidding).isNotNull();
            assertThat(bidding.getAuction()).isEqualTo(auction);
            assertThat(bidding.getBidder()).isEqualTo(anotherActiveUser);
            assertThat(bidding.getBidPrice()).isEqualTo(1100L);
            assertThat(auction.getHighestBidPrice()).isEqualTo(1100L);
        }

        @Test
        @DisplayName("비활성 경매에 입찰 불가")
        void shouldThrowExceptionWhenAuctionIsInactive() {
            // 경매 완료 처리
            auction.complete();

            // 이 테스트에서 실제 사용되는 스터빙
            when(anotherActiveUser.getId()).thenReturn(3L);

            assertThatThrownBy(() ->
                    auction.bid(anotherActiveUser, 1100L)
            )
                    .isInstanceOf(CustomException.class)
                    .extracting("responseCode")
                    .isEqualTo(ResponseCode.AUCTION_NOT_ACTIVE);
        }

        @Test
        @DisplayName("비활성 사용자는 입찰 불가")
        void shouldThrowExceptionWhenBidderIsInactive() {
            // 이 테스트에서 실제 사용되는 스터빙
            when(inactiveUser.isInactive()).thenReturn(true);

            assertThatThrownBy(() ->
                    auction.bid(inactiveUser, 1100L)
            )
                    .isInstanceOf(CustomException.class)
                    .extracting("responseCode")
                    .isEqualTo(ResponseCode.INACTIVE_BIDDER);
        }

        @Test
        @DisplayName("최소 입찰가 미만 입찰 불가")
        void shouldThrowExceptionWhenBidPriceIsBelowMinimum() {
            // 이 테스트에서 실제 사용되는 스터빙
            when(anotherActiveUser.getId()).thenReturn(3L);
            when(anotherActiveUser.isInactive()).thenReturn(false);

            assertThatThrownBy(() ->
                    auction.bid(anotherActiveUser, 1040L)
            )
                    .isInstanceOf(CustomException.class)
                    .extracting("responseCode")
                    .isEqualTo(ResponseCode.BID_INCREMENT_VIOLATION);
        }

        @Test
        @DisplayName("경매 소유자는 본인 경매에 입찰 불가")
        void shouldThrowExceptionWhenBidderIsAuctionOwner() {
            // 이 테스트에서 실제 사용되는 스터빙
            when(activeUser.getId()).thenReturn(1L);
            when(activeUser.isInactive()).thenReturn(false);

            assertThatThrownBy(() ->
                    auction.bid(activeUser, 1100L)
            )
                    .isInstanceOf(CustomException.class)
                    .extracting("responseCode")
                    .isEqualTo(ResponseCode.BID_OWNER_CONFLICT);
        }

        @Test
        @DisplayName("입찰 후 최고 입찰가 변경 확인")
        void shouldUpdateHighestBidPriceAfterBidding() {
            // 이 테스트에서 실제 사용되는 스터빙
            when(anotherActiveUser.getId()).thenReturn(3L);
            when(anotherActiveUser.isInactive()).thenReturn(false);

            // 첫 번째 입찰
            auction.bid(anotherActiveUser, 1100L);
            assertThat(auction.getHighestBidPrice()).isEqualTo(1100L);

            // 새로운 높은 입찰
            User thirdUser = mock(User.class);
            when(thirdUser.getId()).thenReturn(4L);
            when(thirdUser.isInactive()).thenReturn(false);

            auction.bid(thirdUser, 1200L);
            assertThat(auction.getHighestBidPrice()).isEqualTo(1200L);
        }
    }

    @Nested
    @DisplayName("상태 관리 테스트")
    class StatusManagementTests {

        @Test
        @DisplayName("경매 완료 테스트")
        void shouldCompleteAuction() {
            auction.complete();
            assertThat(auction.getStatus()).isEqualTo(AuctionStatus.COMPLETED);
        }

        @Test
        @DisplayName("경매 상태 확인 테스트")
        void shouldCheckIfAuctionIsInactive() {
            assertThat(auction.isInactive()).isFalse();

            auction.complete();
            assertThat(auction.isInactive()).isTrue();
        }
    }
}