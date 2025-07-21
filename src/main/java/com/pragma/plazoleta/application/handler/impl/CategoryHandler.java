package com.pragma.plazoleta.application.handler.impl;

import com.pragma.plazoleta.application.dto.category.CategoryResponseDto;
import com.pragma.plazoleta.application.handler.ICategoryHandler;
import com.pragma.plazoleta.application.mapper.ICategoryResponseMapper;
import com.pragma.plazoleta.domain.usecase.CategoryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryHandler implements ICategoryHandler {
    private final CategoryUseCase categoryUseCase;
    private final ICategoryResponseMapper responseMapper;

    @Override
    public CategoryResponseDto getByName(String name) {
        return responseMapper.toDto(categoryUseCase.getByName(name));
    }

    @Override
    public List<CategoryResponseDto> getAll() {
        return categoryUseCase.getAll().stream()
                .map(responseMapper::toDto)
                .collect(Collectors.toList());
    }
} 