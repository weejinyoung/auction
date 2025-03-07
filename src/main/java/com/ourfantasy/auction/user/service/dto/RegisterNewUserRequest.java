package com.ourfantasy.auction.user.service.dto;

public record RegisterNewUserRequest(
    String nickname,
    String email
) {
}
