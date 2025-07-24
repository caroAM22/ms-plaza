package com.pragma.plazoleta.application.handler.impl;

import com.pragma.plazoleta.application.dto.response.CategoryResponse;
import com.pragma.plazoleta.application.handler.ICategoryHandler;
import com.pragma.plazoleta.application.mapper.ICategoryResponseMapper;
import com.pragma.plazoleta.domain.api.ICategoryServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryHandler implements ICategoryHandler {
    private final ICategoryServicePort categoryServicePort;
    private final ICategoryResponseMapper responseMapper;
    

    @Override
    public List<CategoryResponse> getAll() {
        return categoryServicePort.getAll().stream()
                .map(responseMapper::toDto)
                .toList();
    }
} 