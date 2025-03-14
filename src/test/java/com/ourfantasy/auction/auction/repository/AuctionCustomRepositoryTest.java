package com.ourfantasy.auction.auction.repository;

import com.ourfantasy.auction.auction.model.Auction;
import com.ourfantasy.auction.auction.model.AuctionStatus;
import com.ourfantasy.auction.item.model.Item;
import com.ourfantasy.auction.item.model.ItemCategory;
import com.ourfantasy.auction.user.model.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuctionCustomRepositoryTest {

    @Autowired
    private AuctionCustomRepository auctionCustomRepository;

    @Autowired
    private EntityManager em;

    private User testUser;
    private Map<ItemCategory, List<Auction>> categorizedAuctions = new HashMap<>();

    @BeforeEach
    void setUp() {
        // 테스트 유저 생성
        testUser = User.builderWithValidate()
                .email("test@test.com")
                .nickname("test")
                .build();
        em.persist(testUser);

        // 다양한 카테고리의 경매 생성
        // 1. ACCESSORY 카테고리 (여러 마감 시간)
        List<Auction> accessoryAuctions = new ArrayList<>();
        accessoryAuctions.addAll(createAuctionsWithCategory(testUser, ItemCategory.ACCESSORY, 5, LocalDateTime.now().plusHours(3)));
        accessoryAuctions.addAll(createAuctionsWithCategory(testUser, ItemCategory.ACCESSORY, 3, LocalDateTime.now().plusDays(2)));
        accessoryAuctions.addAll(createAuctionsWithCategory(testUser, ItemCategory.ACCESSORY, 2, LocalDateTime.now().plusHours(12)));
        categorizedAuctions.put(ItemCategory.ACCESSORY, accessoryAuctions);

        // 2. CLOTHING 카테고리
        List<Auction> clothingAuctions = createAuctionsWithCategory(testUser, ItemCategory.CLOTHING, 4, LocalDateTime.now().plusHours(1));
        categorizedAuctions.put(ItemCategory.CLOTHING, clothingAuctions);

        // 3. DIGITAL 카테고리
        List<Auction> digitalAuctions = createAuctionsWithCategory(testUser, ItemCategory.DIGITAL, 3, LocalDateTime.now().plusHours(5));
        categorizedAuctions.put(ItemCategory.DIGITAL, digitalAuctions);

        // 4. SHOES 카테고리
        List<Auction> shoesAuctions = createAuctionsWithCategory(testUser, ItemCategory.SHOES, 2, LocalDateTime.now().plusDays(1));
        categorizedAuctions.put(ItemCategory.SHOES, shoesAuctions);

        // 5. FURNITURE 카테고리
        List<Auction> furnitureAuctions = createAuctionsWithCategory(testUser, ItemCategory.FURNITURE, 3, LocalDateTime.now().plusHours(8));
        categorizedAuctions.put(ItemCategory.FURNITURE, furnitureAuctions);

        // 영속성 컨텍스트 플러시
        em.flush();
    }

    @Test
    @DisplayName("활성화된 경매를 최신순으로 조회 및 Item 정보 함께 로드")
    void findLatestOpenedAuctions() {
        // given
        // 페이지 요청 설정 (첫 페이지, 페이지당 5개, 생성일 기준 내림차순)
        PageRequest pageRequest = PageRequest.of(0, 5, Sort.Direction.DESC, "createdAt");

        // when
        Page<Auction> auctionPage = auctionCustomRepository.findLatestOpenedAuctions(pageRequest);

        // then
        int totalActiveAuctions = categorizedAuctions.values().stream()
                .mapToInt(List::size)
                .sum();

        assertThat(auctionPage).isNotNull();
        assertThat(auctionPage.getContent()).hasSize(5); // 페이지 크기가 5이므로 5개 조회
        assertThat(auctionPage.getTotalElements()).isEqualTo(totalActiveAuctions);

        // 최신순으로 정렬되었는지 확인
        LocalDateTime prevCreatedAt = null;
        for (Auction auction : auctionPage.getContent()) {
            if (prevCreatedAt != null) {
                assertThat(auction.getCreatedAt()).isBeforeOrEqualTo(prevCreatedAt);
            }
            prevCreatedAt = auction.getCreatedAt();

            // Item 정보가 함께 로드되었는지 확인
            assertThat(auction.getItem()).isNotNull();
            assertThat(auction.getStatus()).isEqualTo(AuctionStatus.ACTIVE);
        }
    }

    @Test
    @DisplayName("활성화된 경매를 카테고리별로 마감시간 임박순 조회")
    void findNearestClosingAuctionsByCategory() {
        // given
        ItemCategory targetCategory = ItemCategory.ACCESSORY;
        List<Auction> accessoryAuctions = categorizedAuctions.get(targetCategory);

        // 페이지 설정 (첫 페이지, 5개 항목)
        PageRequest pageRequest = PageRequest.of(0, 5);

        // when
        Page<Auction> auctionPage = auctionCustomRepository.findNearestClosingAuctionsByCategory(
                pageRequest, targetCategory);

        // then
        assertThat(auctionPage).isNotNull();
        assertThat(auctionPage.getContent()).hasSize(5); // 페이지 크기가 5이므로 5개만 조회
        assertThat(auctionPage.getTotalElements()).isEqualTo(accessoryAuctions.size());
        assertThat(auctionPage.getTotalPages()).isEqualTo((int) Math.ceil(accessoryAuctions.size() / 5.0));

        // 마감시간 순으로 정렬되었는지 확인 (오름차순)
        LocalDateTime prevClosingAt = null;
        for (Auction auction : auctionPage.getContent()) {
            if (prevClosingAt != null) {
                assertThat(auction.getClosingAt()).isAfterOrEqualTo(prevClosingAt);
            }
            prevClosingAt = auction.getClosingAt();

            // 카테고리가 ACCESSORY인지 확인
            assertThat(auction.getItem().getCategory()).isEqualTo(targetCategory);

            // Item 정보가 함께 로드되었는지 확인
            assertThat(auction.getItem()).isNotNull();
            assertThat(auction.getStatus()).isEqualTo(AuctionStatus.ACTIVE);
        }

        // 두 번째 페이지 요청 (있는 경우)
        if (auctionPage.getTotalPages() > 1) {
            PageRequest secondPageRequest = PageRequest.of(1, 5);
            Page<Auction> secondPage = auctionCustomRepository.findNearestClosingAuctionsByCategory(
                    secondPageRequest, targetCategory);

            // 두 번째 페이지 검증
            assertThat(secondPage.getNumber()).isEqualTo(1);
            assertThat(secondPage.getContent().size()).isLessThanOrEqualTo(5);
        }
    }

    @Test
    @DisplayName("모든 카테고리에서 각각 마감시간 임박순으로 조회")
    void findNearestClosingAuctionsByAllCategories() {
        // 모든 카테고리에 대해 테스트
        for (ItemCategory category : categorizedAuctions.keySet()) {
            // given
            PageRequest pageRequest = PageRequest.of(0, 10);

            // when
            Page<Auction> auctionPage = auctionCustomRepository.findNearestClosingAuctionsByCategory(
                    pageRequest, category);

            // then
            List<Auction> expectedAuctions = categorizedAuctions.get(category);
            assertThat(auctionPage.getTotalElements()).isEqualTo(expectedAuctions.size());

            // 마감시간 오름차순 정렬 확인
            LocalDateTime prevClosingAt = null;
            for (Auction auction : auctionPage.getContent()) {
                if (prevClosingAt != null) {
                    assertThat(auction.getClosingAt()).isAfterOrEqualTo(prevClosingAt);
                }
                prevClosingAt = auction.getClosingAt();

                // 카테고리 확인
                assertThat(auction.getItem().getCategory()).isEqualTo(category);
            }
        }
    }

    // 특정 카테고리와 마감 시간을 가진 여러 경매 생성 헬퍼 메소드
    private List<Auction> createAuctionsWithCategory(User owner, ItemCategory category, int count, LocalDateTime baseClosingTime) {
        List<Auction> auctions = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Item item = Item.builder()
                    .owner(owner)
                    .name(category.name() + " Item " + i)
                    .detail("Detail for " + category.name() + " " + i)
                    .category(category)
                    .build();
            em.persist(item);

            // 약간의 시간 차이를 두고 마감시간 설정 (순서 보장)
            LocalDateTime closingAt = baseClosingTime.plusMinutes(i * 30);

            Auction auction = Auction.builderWithValidate()
                    .cosigner(owner)
                    .item(item)
                    .startingPrice(1000L * (i + 1))
                    .minimumBidIncrement(100L * (i + 1))
                    .closingAt(closingAt)
                    .build();
            em.persist(auction);
            auctions.add(auction);

            // 생성 시간 차이를 위해 약간의 딜레이
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return auctions;
    }
}