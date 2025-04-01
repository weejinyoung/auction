package com.ourfantasy.auction.user.init;

import com.ourfantasy.auction.rating.model.UserRating;
import com.ourfantasy.auction.rating.repository.UserRatingRepository;
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
public class UserRatingDataInitializerByTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRatingRepository userRatingRepository;

    private final Random random = new Random();

    @Test
    @DisplayName("모든 유저에 대해 UserRating 더미 생성 (페이지 단위, 본인 제외)")
    public void generateUserRatings() {
        int pageSize = 1000;
        int currentPage = 0;
        long totalUsersProcessed = 0;
        long totalRatings = 0;

        while (true) {
            List<User> userBatch = userRepository.findAll(PageRequest.of(currentPage, pageSize)).getContent();
            if (userBatch.isEmpty()) break;

            List<UserRating> ratings = new ArrayList<>();

            for (User ratee : userBatch) {
                // DB에서 랜덤 유저 500명 뽑기
                List<User> randomRaters = userRepository.findRandomUsersForRating();

                // 평가자 후보에서 본인 제외
                List<User> availableRaters = randomRaters.stream()
                        .filter(r -> !r.getId().equals(ratee.getId()))
                        .toList();

                if (availableRaters.isEmpty()) continue;

                int ratingCount = random.nextInt(10) + 50;

                for (int i = 0; i < ratingCount; i++) {
                    User rater = availableRaters.get(random.nextInt(availableRaters.size()));
                    double score = 2.0 + (random.nextDouble() * 3.0);
                    String comment = generateRandomComment();

                    UserRating userRating = UserRating.builder()
                            .rater(rater)
                            .ratee(ratee)
                            .score(score)
                            .comment(comment)
                            .build();

                    ratings.add(userRating);
                }

                totalRatings += ratingCount;
            }

            userRatingRepository.saveAll(ratings);
            totalUsersProcessed += userBatch.size();

            System.out.printf("진행 중: %d명 유저 처리, 누적 평점 수: %d\n", totalUsersProcessed, totalRatings);
            currentPage++;
        }

        System.out.printf("총 %d명의 유저에 대해 %d개의 평점 생성 완료\n", totalUsersProcessed, totalRatings);
    }

    private String generateRandomComment() {
        String[] comments = {
                "정말 친절했어요.",
                "응답이 매우 빨랐어요.",
                "시간 약속을 잘 지켰어요.",
                "믿음이 가는 사람이에요.",
                "매너가 정말 좋아요.",
                "다시 거래하고 싶어요.",
                "친절하고 정직한 분이에요.",
                "거래 내내 기분이 좋았어요.",
                "연락이 원활해서 편했어요.",
                "신뢰할 수 있는 유저였어요."
        };
        return comments[random.nextInt(comments.length)];
    }
}
