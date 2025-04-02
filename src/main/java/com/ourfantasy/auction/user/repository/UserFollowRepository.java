package com.ourfantasy.auction.user.repository;

import com.ourfantasy.auction.user.model.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {
}
