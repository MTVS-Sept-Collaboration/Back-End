package com.homefit.backend.item.service;

import com.homefit.backend.category.item.entity.ItemCategory;
import com.homefit.backend.category.item.repository.ItemCategoryRepository;
import com.homefit.backend.item.dto.ItemModifyRequestDto;
import com.homefit.backend.item.dto.ItemResponseDto;
import com.homefit.backend.item.entity.Item;
import com.homefit.backend.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemCategoryRepository itemCategoryRepository;

    public Long save(Item item) {
        return itemRepository.save(item).getId();
    }

    @Transactional(readOnly = true)
    public Item findById(Long id) {
        return itemRepository.findById(id).
                orElseThrow(IllegalArgumentException::new);
    }

    @Transactional(readOnly = true)
    public List<Item> findByItemCategory(String itemCategory) {

        if (itemCategory == null || itemCategory.isEmpty()) {
            throw new IllegalArgumentException("itemCategory is null or empty");
        }

        ItemCategory foundCategory = itemCategoryRepository.findByName(itemCategory)
                .orElseThrow(() -> new IllegalArgumentException("itemCategory not found"));

        return itemRepository.findByItemCategory(foundCategory);
    }

    @Transactional(readOnly = true)
    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Long update(Long id, ItemModifyRequestDto requestDto) {
        Item item = itemRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);

        item.update(requestDto);

        return item.getId();
    }

    public void delete(Long id) {
        Item foundItem = itemRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);

        itemRepository.delete(foundItem);
    }
}
