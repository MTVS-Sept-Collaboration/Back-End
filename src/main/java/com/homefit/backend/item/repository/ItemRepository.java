package com.homefit.backend.item.repository;

import com.homefit.backend.category.item.entity.ItemCategory;
import com.homefit.backend.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByItemCategory(ItemCategory category);
}
