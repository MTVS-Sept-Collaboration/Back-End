package com.homefit.backend.Item.repository;

import com.homefit.backend.Item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
