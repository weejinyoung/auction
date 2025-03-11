package com.ourfantasy.auction.user;

import com.ourfantasy.auction.user.model.User;
import com.ourfantasy.auction.user.repository.UserRepository;
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

    @Test
    @EnabledIf("false") // 반드시 필요할 때만 true 로 해주세요
    @DisplayName("대량의 사용자 데이터 생성 (20,000명)")
    void createBulkUserData() {
        int totalUsers = 20000;
        int batchSize = 500;

        for (int i = 0; i < totalUsers; i += batchSize) {
            List<User> userBatch = new ArrayList<>();

            for (int j = 0; j < batchSize && (i + j) < totalUsers; j++) {
                int userNumber = i + j + 1;
                String nickname = "user" + userNumber;
                String email = "user" + userNumber + "@gmail.com";
                User user = User.createUser(nickname, email);
                userBatch.add(user);
            }
            userRepository.saveAll(userBatch);
        }

        long actualUserCount = userRepository.count();
        System.out.println("총 사용자 수: " + actualUserCount);
    }

}