package com.ourfantasy.auction.user.init;

import com.ourfantasy.auction.user.model.User;
import com.ourfantasy.auction.user.repository.UserRepository;
import com.ourfantasy.auction.util.RandomGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class UserDataInitializerByTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RandomGenerator randomGenerator;

    @Test
    @EnabledIf("false") // 필요할 때만 true로 변경
    @DisplayName("대규모 사용자 데이터 생성 (50,000명)")
    void createLargeUserDataset() {
        int totalUsers = 50000;
        int batchSize = 1000;

        System.out.println("사용자 데이터 생성 시작...");
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < totalUsers; i += batchSize) {
            List<User> userBatch = new ArrayList<>(batchSize);
            int currentBatchSize = Math.min(batchSize, totalUsers - i);

            for (int j = 0; j < currentBatchSize; j++) {
                // RandomGenerator를 사용하여 닉네임 생성 (중복 허용)
                String nickname = randomGenerator.generateRandomNickname();

                // 이메일 생성 (고유성 보장)
                String email = "user" + (i + j) + "@example.com";

                // 사용자 생성
                User user = User.builderWithValidate()
                        .nickname(nickname)
                        .email(email)
                        .build();

                userBatch.add(user);
            }

            // 배치 저장
            userRepository.saveAll(userBatch);

            // 진행 상황 로깅
            if ((i / batchSize) % 10 == 0) {
                System.out.printf("진행 중: %d/%d 사용자 생성됨 (%.1f%%)\n",
                        i + currentBatchSize, totalUsers,
                        (i + currentBatchSize) * 100.0 / totalUsers);
            }
        }

        long endTime = System.currentTimeMillis();
        double elapsedSeconds = (endTime - startTime) / 1000.0;

        System.out.println("데이터 생성 완료!");
        System.out.printf("생성된 사용자: %d명, 소요 시간: %.2f초 (초당 %.1f명)\n",
                totalUsers, elapsedSeconds, (totalUsers / elapsedSeconds));
    }
}