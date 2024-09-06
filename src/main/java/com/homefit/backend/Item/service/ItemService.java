package com.homefit.backend.Item.service;

import com.homefit.backend.Item.entity.Item;
import com.homefit.backend.Item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public Long save(Item item) {
        return itemRepository.save(item).getId();
    }

    public Item findById(Long id) {
        return itemRepository.findById(id).
                orElseThrow(IllegalArgumentException::new);
    }

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public void delete(Long id) {
        Item foundItem = itemRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);

        itemRepository.delete(foundItem);
    }
}
