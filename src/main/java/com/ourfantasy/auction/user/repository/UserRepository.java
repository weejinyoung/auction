package com.ourfantasy.auction.user.repository;

import com.ourfantasy.auction.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT * FROM user ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<User> findRandomUsers(@Param("limit") int limit);
}
