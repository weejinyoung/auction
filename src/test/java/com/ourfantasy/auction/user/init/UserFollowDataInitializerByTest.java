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

    @Test
    @DisplayName("유저 간의 팔로우 관계 더미 데이터 생성 (1명당 10~1000명)")
    public void generateUserFollowRelations() {
        List<User> allUsers = userRepository.findAll();

        List<UserFollow> follows = new ArrayList<>();

        for (User follower : allUsers) {
            Set<Long> followeeIds = new HashSet<>();

            // 각 유저가 팔로우할 수 있는 수: 최소 10 ~ 최대 300
            int followCount = random.nextInt(300) + 10;  // (0~290) + 10 → 10~300

            for (int i = 0; i < followCount; i++) {
                User followee;
                do {
                    followee = allUsers.get(random.nextInt(allUsers.size()));
                } while (
                        followee.getId().equals(follower.getId()) ||  // 자기 자신은 안됨
                                !followeeIds.add(followee.getId())            // 중복도 안됨
                );

                UserFollow userFollow = new UserFollow(follower, followee);
                follows.add(userFollow);
            }
        }

        userFollowRepository.saveAll(follows);
        System.out.printf("✅ 총 %d개의 팔로우 관계 생성 완료\n", follows.size());
    }
}
