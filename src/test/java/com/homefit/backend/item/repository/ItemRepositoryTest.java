package com.homefit.backend.item.repository;

import com.homefit.backend.item.dto.ItemModifyRequestDto;
import com.homefit.backend.item.dto.ItemSaveRequestDto;
import com.homefit.backend.item.entity.Item;
import com.homefit.backend.item.service.ItemService;
import com.homefit.backend.itemcategory.entity.ItemCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemRepositoryTest {

    @Autowired
    private ItemService itemService;

    private Item item;

    @BeforeEach
    void setUp() {
        ItemSaveRequestDto requestDto = new ItemSaveRequestDto();
        requestDto.setItemCategory(new ItemCategory("테스트용 카테고리"));

        item = requestDto.toEntity();
    }

    @DisplayName("아이템 생성 테스트")
    @Test
    void saveUserTest() {

        itemService.save(item);

        assertThat(itemService.findAll().size()).isEqualTo(1);
    }

    @DisplayName("아이템 리스트 조회 테스트")
    @Test
    void findItemListTest() {

        itemService.save(item);

        ItemSaveRequestDto requestDto = new ItemSaveRequestDto();
        requestDto.setItemCategory(new ItemCategory("테스트용 카테고리2"));

        Item newItem = requestDto.toEntity();

        itemService.save(newItem);

        List<Item> itemList = itemService.findAll();

        assertThat(itemList.size()).isEqualTo(2);
    }

    @DisplayName("아이템 아이디 기반 단일 조회 테스트")
    @Test
    void findItemTest() {

        Long savedId = itemService.save(item);

        Long foundId = itemService.findById(savedId).getId();

        assertThat(foundId).isEqualTo(savedId);
    }

    @DisplayName("아이템 수정 테스트")
    @Test
    void updateItemTest() {

        Long savedId = itemService.save(item);

        ItemModifyRequestDto requestDto = new ItemModifyRequestDto();
        requestDto.setItemCategory(new ItemCategory("업데이트용 카테고리"));

        itemService.update(savedId, requestDto);

        assertThat(itemService.findById(savedId).getItemCategory().getName()).isEqualTo("업데이트용 카테고리");
    }

    @DisplayName("아이템 삭제 테스트")
    @Test
    void deleteItemTest() {

        Long savedId = itemService.save(item);

        itemService.delete(savedId);

        assertThrows(IllegalArgumentException.class, () -> {
            itemService.findById(savedId);
        });
    }
}