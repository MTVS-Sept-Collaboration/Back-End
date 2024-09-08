package com.homefit.backend.item.service;

import com.homefit.backend.item.dto.ItemModifyRequestDto;
import com.homefit.backend.item.entity.Item;
import com.homefit.backend.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
