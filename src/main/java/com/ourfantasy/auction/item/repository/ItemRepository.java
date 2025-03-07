package com.ourfantasy.auction.item.repository;

import com.ourfantasy.auction.item.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
