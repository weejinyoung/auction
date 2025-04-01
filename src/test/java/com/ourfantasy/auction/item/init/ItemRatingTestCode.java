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
public class ItemRatingTestCode {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRatingRepository itemRatingRepository;

    private final Random random = new Random();

    @Test
    @DisplayName("아이템 10개에 대해 평점 3개씩 생성하여 저장 테스트")
    public void generateSampleItemRatings() {
        // 아이템 10개만 조회
        List<Item> items = itemRepository.findAll(PageRequest.of(0, 10)).getContent();
        List<User> allUsers = userRepository.findAll();

        List<ItemRating> ratings = new ArrayList<>();

        for (Item item : items) {
            List<User> availableRaters = allUsers.stream()
                    .filter(user -> !user.getId().equals(item.getOwner().getId()))
                    .toList();

            for (int i = 0; i < 3; i++) {
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
        }

        itemRatingRepository.saveAll(ratings);

        System.out.printf("✅ 총 %d개의 ItemRating 저장 완료\n", ratings.size());
    }

    private String generateItemComment() {
        String[] comments = {
                "상태가 좋아요", "가성비 최고입니다", "거래 만족합니다",
                "약간 사용감은 있어요", "빠른 배송 감사합니다", "추천하고 싶어요"
        };
        return comments[random.nextInt(comments.length)];
    }
}
