package com.pragma.plazoleta.application.handler.impl;

import com.pragma.plazoleta.application.dto.response.CategoryResponse;
import com.pragma.plazoleta.application.handler.ICategoryHandler;
import com.pragma.plazoleta.application.mapper.ICategoryMapper;
import com.pragma.plazoleta.domain.api.ICategoryServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryHandler implements ICategoryHandler {
    private final ICategoryServicePort categoryServicePort;
    private final ICategoryMapper categoryMapper;

    @Override
    public List<CategoryResponse> getAll() {
        return categoryServicePort.getAll().stream()
                .map(categoryMapper::toCategoryResponse)
                .toList();
    }
} 