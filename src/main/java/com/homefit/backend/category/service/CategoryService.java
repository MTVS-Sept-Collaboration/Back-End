package com.homefit.backend.category.service;

import com.homefit.backend.category.dto.CategoryRequest;
import com.homefit.backend.category.dto.CategoryResponse;
import com.homefit.backend.category.entity.Category;
import com.homefit.backend.category.repository.CategoryRepository;
import com.homefit.backend.global.exception.ErrorCode;
import com.homefit.backend.global.exception.model.ConflictException;
import com.homefit.backend.global.exception.model.NotFoundException;
import com.homefit.backend.global.exception.model.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // 카테고리 이름 유효성 검사
    public void validateCategoryName(String name) {
        if (name == null || name.isEmpty()) {
            throw new ValidationException("카테고리 이름은 null이거나 비어 있을 수 없습니다.", ErrorCode.VALIDATION_EXCEPTION);
        }
        // 동일한 이름이 존재하는지 확인
        boolean exists = categoryRepository.existsByName(name);
        if (exists) {
            throw new ConflictException("이미 존재하는 카테고리 이름입니다: " + name, ErrorCode.CONFLICT_EXCEPTION);
        }
    }

    // 카테고리 수정 시 유효성 검사
    public void validateUpdateCategory(String newName, Category category) {
        if (newName == null || newName.isEmpty()) {
            throw new ValidationException("새 카테고리 이름은 null이거나 비어 있을 수 없습니다.", ErrorCode.VALIDATION_EXCEPTION);
        }

        if (category.getName().equals(newName)) {
            throw new ConflictException("새 카테고리 이름은 현재 이름과 같을 수 없습니다.", ErrorCode.CONFLICT_EXCEPTION);
        }

        // 동일한 이름이 존재하는지 확인
        boolean exists = categoryRepository.existsByName(newName);
        if (exists) {
            throw new ConflictException("이미 존재하는 카테고리 이름입니다: " + newName, ErrorCode.CONFLICT_EXCEPTION);
        }
    }

    // 카테고리 생성
    public Category createCategory(CategoryRequest categoryRequest) {
        // 카테고리 이름 유효성 및 중복 체크
        validateCategoryName(categoryRequest.getName());

        Category category = new Category(categoryRequest.getName());
        return categoryRepository.save(category);
    }

    // 카테고리 수정
    public Category updateCategory(Long id, CategoryRequest categoryRequest) {
        // 카테고리 존재 여부 확인
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("카테고리를 찾을 수 없습니다.", ErrorCode.NOT_FOUND_EXCEPTION));

        // 새 이름 유효성 및 중복 체크
        validateUpdateCategory(categoryRequest.getName(), category);

        category.updateCategory(categoryRequest.getName());
        return categoryRepository.save(category);
    }

    // 카테고리 삭제
    public void deleteCategory(Long id) {
        // 카테고리 존재 여부 확인
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("카테고리를 찾을 수 없습니다.", ErrorCode.NOT_FOUND_EXCEPTION));

        categoryRepository.delete(category);
    }

    // 특정 카테고리 조회
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("카테고리를 찾을 수 없습니다. ID: " + id));

        return CategoryResponse.builder()
                .name(category.getName())
                .build();
    }

    // 전체 카테고리 조회
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(category -> CategoryResponse.builder()
                        .name(category.getName())
                        .build())
                .collect(Collectors.toList());
    }

}
