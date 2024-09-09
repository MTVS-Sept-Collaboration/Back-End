package com.homefit.backend.category.item.repository;

import com.homefit.backend.category.item.entity.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemCategoryRepository extends JpaRepository<ItemCategory, Long> {
    boolean existsByName(String name);

    Optional<ItemCategory> findByName(String name);
}
