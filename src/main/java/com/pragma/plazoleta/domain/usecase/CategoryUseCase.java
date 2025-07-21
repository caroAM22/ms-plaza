package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.spi.ICategoryPersistencePort;
import com.pragma.plazoleta.domain.exception.DomainException;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CategoryUseCase {
    private final ICategoryPersistencePort categoryPersistencePort;

    public Category getByName(String name) {
        return categoryPersistencePort.getByName(name)
                .orElseThrow(() -> new DomainException("Category not found"));
    }

    public List<Category> getAll() {
        return categoryPersistencePort.getAll();
    }
} 