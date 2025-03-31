package com.ourfantasy.auction.rating.repository;

import com.ourfantasy.auction.rating.model.ItemRating;
import com.ourfantasy.auction.rating.model.UserRating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRatingRepository extends JpaRepository<ItemRating, Long> {
}
