package com.ourfantasy.auction.item.repository;

import com.ourfantasy.auction.item.model.Item;
import com.ourfantasy.auction.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwner(User user);
}
