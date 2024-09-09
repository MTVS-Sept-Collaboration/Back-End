package com.homefit.backend.category.item.service;

import com.homefit.backend.category.item.dto.ItemCategoryRequest;
import com.homefit.backend.category.item.dto.ItemCategoryResponse;
import com.homefit.backend.category.item.repository.ItemCategoryRepository;
import com.homefit.backend.category.item.entity.ItemCategory;
import com.homefit.backend.global.exception.ErrorCode;
import com.homefit.backend.global.exception.model.ConflictException;
import com.homefit.backend.global.exception.model.NotFoundException;
import com.homefit.backend.global.exception.model.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemCategoryService {

    private final ItemCategoryRepository itemCategoryRepository;

    @Autowired
    public ItemCategoryService(ItemCategoryRepository itemCategoryRepository) {
        this.itemCategoryRepository = itemCategoryRepository;
    }



    // 카테고리 이름 유효성 검사
    public void validateCategoryName(String name) {
        log.info("카테고리 이름 유효성 검사 시작: {}", name);
        if (name == null || name.isEmpty()) {
            log.error("유효성 검사 실패: 카테고리 이름이 비어있음");
            throw new ValidationException("카테고리 이름은 null이거나 비어 있을 수 없습니다.", ErrorCode.VALIDATION_EXCEPTION);
        }
        boolean exists = itemCategoryRepository.existsByName(name);
        if (exists) {
            log.error("카테고리 이름 중복: {}", name);
            throw new ConflictException("이미 존재하는 카테고리 이름입니다: " + name, ErrorCode.CONFLICT_EXCEPTION);
        }
    }

    // 카테고리 수정 시 유효성 검사
    public void validateUpdateCategory(String newName, ItemCategory itemCategory) {
        log.info("카테고리 수정 유효성 검사 시작: 기존 이름={}, 새 이름={}", itemCategory.getName(), newName);

        if (newName == null || newName.isEmpty()) {
            log.error("유효성 검사 실패: 새 카테고리 이름이 null이거나 비어 있음");
            throw new ValidationException("새 카테고리 이름은 null이거나 비어 있을 수 없습니다.", ErrorCode.VALIDATION_EXCEPTION);
        }

        if (itemCategory.getName().equals(newName)) {
            log.error("유효성 검사 실패: 새 이름이 현재 이름과 동일함");
            throw new ConflictException("새 카테고리 이름은 현재 이름과 같을 수 없습니다.", ErrorCode.CONFLICT_EXCEPTION);
        }

        boolean exists = itemCategoryRepository.existsByName(newName);
        if (exists) {
            log.error("유효성 검사 실패: 동일한 이름의 카테고리가 이미 존재함: {}", newName);
            throw new ConflictException("이미 존재하는 카테고리 이름입니다: " + newName, ErrorCode.CONFLICT_EXCEPTION);
        }

        log.info("카테고리 수정 유효성 검사 통과: 새 이름={}", newName);
    }

    // 카테고리 생성
    public ItemCategory createCategory(ItemCategoryRequest itemCategoryRequest) {
        log.info("카테고리 생성 요청: {}", itemCategoryRequest.getName());
        validateCategoryName(itemCategoryRequest.getName());

        ItemCategory itemCategory = new ItemCategory(itemCategoryRequest.getName());
        ItemCategory savedCategory = itemCategoryRepository.save(itemCategory);
        log.info("카테고리 생성 완료: {}", savedCategory.getId());
        return savedCategory;
    }

    // 카테고리 수정
    public ItemCategory updateCategory(Long id, ItemCategoryRequest itemCategoryRequest) {
        log.info("카테고리 수정 요청: ID={}, 새 이름={}", id, itemCategoryRequest.getName());
        ItemCategory itemCategory = itemCategoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("카테고리 ID {} 찾을 수 없음", id);
                    return new NotFoundException("카테고리를 찾을 수 없습니다.", ErrorCode.NOT_FOUND_EXCEPTION);
                });

        validateUpdateCategory(itemCategoryRequest.getName(), itemCategory);

        itemCategory.updateCategory(itemCategoryRequest.getName());
        ItemCategory updatedCategory = itemCategoryRepository.save(itemCategory);
        log.info("카테고리 수정 완료: ID={}", updatedCategory.getId());
        return updatedCategory;
    }

    // 카테고리 삭제
    public void deleteCategory(Long id) {
        log.info("카테고리 삭제 요청: ID={}", id);
        ItemCategory itemCategory = itemCategoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("카테고리 ID {} 찾을 수 없음", id);
                    return new NotFoundException("카테고리를 찾을 수 없습니다.", ErrorCode.NOT_FOUND_EXCEPTION);
                });

        itemCategoryRepository.delete(itemCategory);
        log.info("카테고리 삭제 완료: ID={}", id);
    }

    // 특정 카테고리 조회
    public ItemCategoryResponse getItemCategoryById(Long id) {
        log.info("특정 카테고리 조회 요청: ID={}", id);
        ItemCategory itemCategory = itemCategoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("카테고리 ID {} 찾을 수 없음", id);
                    return new NotFoundException("카테고리를 찾을 수 없습니다. ID: " + id, ErrorCode.NOT_FOUND_EXCEPTION);
                });

        ItemCategoryResponse response = ItemCategoryResponse.builder()
                .name(itemCategory.getName())
                .createdAt(itemCategory.getCreatedAt())
                .updatedAt(itemCategory.getUpdatedAt())
                .build();
        log.info("카테고리 조회 성공: ID={}", id);
        return response;
    }

    // 전체 카테고리 조회
    public List<ItemCategoryResponse> getAllItemCategories() {
        log.info("전체 카테고리 조회 요청");
        List<ItemCategoryResponse> categories = itemCategoryRepository.findAll()
                .stream()
                .map(itemCategory -> ItemCategoryResponse.builder()
                        .name(itemCategory.getName())
                        .createdAt(itemCategory.getCreatedAt())
                        .updatedAt(itemCategory.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
        log.info("전체 카테고리 조회 완료, 총 개수: {}", categories.size());
        return categories;
    }

}
