package com.ourfantasy.auction.user.service.dto;

import com.ourfantasy.auction.user.model.User;

public record RegisterNewUserResponse(
    String nickname,
    String email
) {
    public static RegisterNewUserResponse from(User user) {
        return new RegisterNewUserResponse(user.getNickname(), user.getEmail());
    }
}
