package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.spi.ICategoryPersistencePort;
import com.pragma.plazoleta.domain.exception.DomainException;
import com.pragma.plazoleta.domain.api.ICategoryServicePort;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CategoryUseCase implements ICategoryServicePort {
    private final ICategoryPersistencePort categoryPersistencePort;

    @Override
    public Category getByName(String name) {
        return categoryPersistencePort.getByName(name)
                .orElseThrow(() -> new DomainException("Category not found"));
    }

    @Override
    public List<Category> getAll() {
        return categoryPersistencePort.getAll();
    }
} 