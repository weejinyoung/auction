package com.ourfantasy.auction.auction.repository;

import com.ourfantasy.auction.auction.model.Auction;
import com.ourfantasy.auction.auction.model.AuctionStatus;
import com.ourfantasy.auction.item.model.Item;
import com.ourfantasy.auction.user.model.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuctionCustomRepositoryTest {

    @Autowired
    private AuctionCustomRepository auctionCustomRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("활성화된 경매를 최신순으로 조회 및 Item 정보 함께 로드")
    void findRecentActiveAuctionsWithItem() {
        // given
        User owner = createAndPersistUser();

        // 페이지 요청 설정 (첫 페이지, 페이지당 3개, 생성일 기준 내림차순)
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "createdAt");

        // when
        Page<Auction> auctionPage = auctionCustomRepository.findRecentActiveAuctionsWithItem(pageRequest);

        // then
        assertThat(auctionPage).isNotNull();
        assertThat(auctionPage.getContent()).hasSize(3); // 페이지 크기가 3이므로 3개 조회
        assertThat(auctionPage.getTotalElements()).isEqualTo(5); // 전체 개수는 5개
        assertThat(auctionPage.getTotalPages()).isEqualTo(2); // 총 페이지 수는 2페이지

        // 최신순으로 정렬되었는지 확인
        LocalDateTime prevDate = null;
        for (Auction auction : auctionPage.getContent()) {
            if (prevDate != null) {
                assertThat(auction.getCreatedAt()).isBeforeOrEqualTo(prevDate);
            }
            prevDate = auction.getCreatedAt();

            // Item 정보가 함께 로드되었는지 확인
            assertThat(auction.getItem()).isNotNull();
            assertThat(auction.getItem().getName()).startsWith("Item");
            assertThat(auction.getItem().getDetail()).startsWith("Detail");
        }
    }

    @Test
    @DisplayName("활성화된 경매가 없는 경우 빈 결과 반환")
    void findRecentActiveAuctionsWithItem_emptyResult() {
        // given
        User owner = createAndPersistUser();
        List<Auction> auctions = createAndPersistAuctions(owner, 3);

        // 모든 경매를 COMPLETED 상태로 변경
        auctions.forEach(auction -> {
            auction.complete();
            em.merge(auction);
        });
        em.flush();

        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<Auction> auctionPage = auctionCustomRepository.findRecentActiveAuctionsWithItem(pageRequest);

        // then
        assertThat(auctionPage).isNotNull();
        assertThat(auctionPage.getContent()).isEmpty();
        assertThat(auctionPage.getTotalElements()).isZero();
    }

    // 테스트용 유저 생성 및 영속화
    private User createAndPersistUser() {
        User user = User.createUser("testuser", "test@test.com");
        em.persist(user);
        return user;
    }

    // 테스트용 경매와 아이템 생성 및 영속화
    private List<Auction> createAndPersistAuctions(User owner, int count) {
        List<Auction> auctions = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            // Item 생성
            Item item = Item.createItem(owner, "Item " + i, "Detail " + i);
            em.persist(item);

            // Auction 생성
            LocalDateTime closingAt = LocalDateTime.now().plusDays(i + 1);
            Auction auction = Auction.createAuction(
                    owner,
                    item,
                    1000L * (i + 1), // 시작가
                    100L, // 최소 입찰 증가액
                    closingAt
            );
            em.persist(auction);
            auctions.add(auction);

            // 생성 시간 차이를 위해 슬립 (실제 테스트에서는 필요 없을 수 있음)
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        em.flush();
        return auctions;
    }
}