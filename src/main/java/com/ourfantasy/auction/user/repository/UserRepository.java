package com.ourfantasy.auction.user.repository;

import com.ourfantasy.auction.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
