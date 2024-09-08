package com.homefit.backend.item.controller;

import com.homefit.backend.item.dto.ItemListResponseDto;
import com.homefit.backend.item.dto.ItemModifyRequestDto;
import com.homefit.backend.item.dto.ItemResponseDto;
import com.homefit.backend.item.dto.ItemSaveRequestDto;
import com.homefit.backend.item.entity.Item;
import com.homefit.backend.item.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/item")
@Tag(name = "아이템 API", description = "아이템 CRUD API")
public class ItemController {

    private final ItemService itemService;

    // 아이템 생성
    @Operation(summary = "아이템 생성", description = "아이템을 생성합니다.")
    @PostMapping
    public ResponseEntity<?> saveItem(@RequestBody @Valid ItemSaveRequestDto requestDto) {

        Item entity = requestDto.toEntity();

        Long savedId = itemService.save(entity);

        return ResponseEntity.ok(savedId);
    }

    // 아이템 단일 조회(ID 기반)
    @Operation(summary = "단일 아이템 조회", description = "아이템을 ID 기반으로 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDto> getItemById(@PathVariable Long id) {

        Item item = itemService.findById(id);

        if (item == null) {
            return ResponseEntity.notFound().build();
        }

        ItemResponseDto responseDto = new ItemResponseDto();
        responseDto.setId(item.getId());
        responseDto.setCategory(item.getItemCategory());

        return ResponseEntity.ok(responseDto);
    }

    // 아이템 전체 조회
    @Operation(summary = "아이템 전체 조회", description = "아이템을 전체를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ItemListResponseDto>> getItemList() {

        List<Item> itemList = itemService.findAll();

        List<ItemListResponseDto> responseDtoList = itemList.stream()
                .map(item -> new ItemListResponseDto(item.getId(), item.getItemCategory()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDtoList);
    }

    // 아이템 수정
    @Operation(summary = "아이템 수정", description = "아이템을 ID 기반으로 수정합니다.")
    @PostMapping("/{id}")
    public ResponseEntity<?> updateItem(
            @PathVariable Long id,
            @RequestBody @Valid ItemModifyRequestDto requestDto) {

        Item foundItem = itemService.findById(id);

        if (foundItem == null) {
            return ResponseEntity.notFound().build();
        }

        foundItem.update(requestDto);

        return ResponseEntity.ok(foundItem.getId());
    }

    // 아이템 삭제
    @Operation(summary = "아이템 삭제", description = "아이템을 ID 기반으로 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        Item item = itemService.findById(id);

        if (item == null) {
            return ResponseEntity.notFound().build();
        }

        itemService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
