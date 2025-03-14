package com.ourfantasy.auction.user.service;

import com.ourfantasy.auction.user.model.User;
import com.ourfantasy.auction.user.repository.UserRepository;
import com.ourfantasy.auction.user.service.dto.RegisterNewUserRequest;
import com.ourfantasy.auction.user.service.dto.RegisterNewUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public RegisterNewUserResponse registerNewUser(RegisterNewUserRequest request) {
        User newUser = User.builderWithValidate()
                .nickname(request.nickname())
                .email(request.email())
                .build();

        User savedUser = userRepository.save(newUser);
        return RegisterNewUserResponse.from(savedUser);
    }
}
