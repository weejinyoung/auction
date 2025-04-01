package com.ourfantasy.auction.item.init;

import com.ourfantasy.auction.item.model.Item;
import com.ourfantasy.auction.item.repository.ItemRepository;
import com.ourfantasy.auction.rating.model.ItemRating;
import com.ourfantasy.auction.rating.repository.ItemRatingRepository;
import com.ourfantasy.auction.user.model.User;
import com.ourfantasy.auction.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootTest
public class ItemRatingDataInitializerByTest {

    private final Random random = new Random();

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRatingRepository itemRatingRepository;

    @Test
    @DisplayName("기존 아이템에 대해 랜덤한 ItemRating 생성 (페이지 단위, 본인 제외, 진행률 출력)")
    public void generateItemRatingsForExistingItems() {
        long startTime = System.currentTimeMillis();

        // 랜덤 유저 500명만 평가자로 뽑기
        List<User> randomUsersForRating = userRepository.findRandomUsersForRating();

        long totalItemCount = itemRepository.count(); // 전체 아이템 수
        int lastLoggedProgress = 0;

        int pageSize = 1000;
        int currentPage = 0;
        long totalItemsProcessed = 0;
        long totalRatings = 0;
        System.out.println("======================== 데이터 생성 시작 ========================");

        while (true) {
            List<Item> itemBatch = itemRepository.findAll(PageRequest.of(currentPage, pageSize)).getContent();
            if (itemBatch.isEmpty()) break;

            List<ItemRating> ratings = new ArrayList<>();

            for (Item item : itemBatch) {
                // 아이템 소유자 제외한 평가자 필터링
                List<User> availableRaters = randomUsersForRating.stream()
                        .filter(rater -> !rater.getId().equals(item.getOwner().getId()))
                        .toList();

                if (availableRaters.isEmpty()) continue;

                int ratingCount = random.nextInt(100) + 200; // 100~2000개

                for (int i = 0; i < ratingCount; i++) {
                    User rater = availableRaters.get(random.nextInt(availableRaters.size()));
                    double score = Math.round((2.0 + random.nextDouble() * 3.0) * 100) / 100.0;
                    String comment = generateItemComment();

                    ItemRating rating = ItemRating.builderWithItemRating()
                            .rater(rater)
                            .item(item)
                            .score(score)
                            .comment(comment)
                            .build();

                    ratings.add(rating);
                }

                totalRatings += ratingCount;
            }

            itemRatingRepository.saveAll(ratings);
            totalItemsProcessed += itemBatch.size();

            // 10% 단위 진행률 로그 출력
            int currentProgress = (int)((double) totalItemsProcessed / totalItemCount * 100);
            if (currentProgress / 10 > lastLoggedProgress) {
                lastLoggedProgress = currentProgress / 10;
                System.out.printf("⏳ 진행률: %d%% 완료 (%d / %d 아이템)\n",
                        lastLoggedProgress * 10, totalItemsProcessed, totalItemCount);
            }

            currentPage++;
        }

        long endTime = System.currentTimeMillis();
        double elapsedSeconds = (endTime - startTime) / 1000.0;

        System.out.printf("✅ 총 %d개의 아이템에 대해 %d개의 평점 생성 완료 (소요 시간: %.2f초)\n",
                totalItemsProcessed, totalRatings, elapsedSeconds);
    }

    private String generateItemComment() {
        String[] comments = {
                "상태가 생각보다 더 좋았어요.",
                "사진이랑 거의 똑같아요!",
                "사용감은 있지만 기능엔 문제 없어요.",
                "설명보다 더 괜찮은 물건이었어요.",
                "가성비가 정말 좋아요.",
                "잘 작동하고 만족스러웠어요.",
                "포장도 깔끔하고 문제 없었어요.",
                "조금 오래돼 보이지만 쓸만해요.",
                "설명과 다른 부분이 조금 있었어요.",
                "기대보다 별로였어요. 다음엔 신중하게 볼게요."
        };
        return comments[random.nextInt(comments.length)];
    }
}
