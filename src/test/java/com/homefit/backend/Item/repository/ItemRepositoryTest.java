package com.homefit.backend.Item.repository;

import com.homefit.backend.Item.dto.ItemSaveRequestDto;
import com.homefit.backend.Item.entity.Item;
import com.homefit.backend.Item.service.ItemService;
import com.homefit.backend.itemcategory.entity.ItemCategory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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

        Long savedId = itemService.save(item);

        assertThat(itemService.findAll().size()).isEqualTo(1);
    }

}