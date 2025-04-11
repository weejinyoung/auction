package com.ourfantasy.auction.user.init;

import com.ourfantasy.auction.user.model.User;
import com.ourfantasy.auction.user.model.UserFollow;
import com.ourfantasy.auction.user.repository.UserFollowRepository;
import com.ourfantasy.auction.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
public class UserFollowDataInitializerByTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFollowRepository userFollowRepository;

    private final Random random = new Random();

    private static final int BATCH_SIZE = 10_000;

    @Test
    @DisplayName("유저 간의 팔로우 관계 더미 데이터 생성 (1명당 10~300명)")
    public void generateUserFollowRelations() {
        long startTime = System.currentTimeMillis();
        System.out.println("팔로우 관계 생성 시작...");

        List<User> allUsers = userRepository.findAll();
        List<UserFollow> follows = new ArrayList<>(BATCH_SIZE);

        int processedUsers = 0;
        long totalFollows = 0;

        for (User follower : allUsers) {
            // 후보 리스트를 복사해서 섞은 뒤 본인을 제외
            List<User> candidates = new ArrayList<>(allUsers);
            candidates.remove(follower);
            Collections.shuffle(candidates);

            int followCount = random.nextInt(291) + 10; // 10~300
            List<User> selectedFollowees = candidates.subList(0, Math.min(followCount, candidates.size()));

            for (User followee : selectedFollowees) {
                UserFollow follow = new UserFollow(follower, followee);
                follows.add(follow);
                totalFollows++;
            }

            processedUsers++;

            // 일정 개수마다 저장
            if (follows.size() >= BATCH_SIZE) {
                userFollowRepository.saveAll(follows);
                follows.clear();
                System.out.printf("💾 중간 저장: %d명 유저의 팔로우 생성 완료 (총 %d건)\n", processedUsers, totalFollows);
            }

            // 진행 로그
            if (processedUsers % 1000 == 0 || processedUsers == allUsers.size()) {
                System.out.printf("🔄 진행 중: %d명 처리 완료\n", processedUsers);
            }
        }

        // 남은 데이터 저장
        if (!follows.isEmpty()) {
            userFollowRepository.saveAll(follows);
        }

        long endTime = System.currentTimeMillis();
        double elapsedSeconds = (endTime - startTime) / 1000.0;

        System.out.printf("✅ 총 %d개의 팔로우 관계 생성 완료 (소요 시간: %.2f초, 초당 %.1f건)\n",
                totalFollows, elapsedSeconds, totalFollows / elapsedSeconds);
    }
}