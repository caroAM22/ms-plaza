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
    public List<Category> getAll() {
        return categoryPersistencePort.getAll();
    }

    @Override
    public Category getByName(String name) {
        return categoryPersistencePort.getByName(name)
                .orElseThrow(() -> new DomainException("Category not found"));
    }

    @Override
    public Category getById(Integer id) {
        return categoryPersistencePort.getById(id)
                .orElseThrow(() -> new DomainException("Category not found"));
    }

    @Override
    public boolean existsById(Integer id) {
        return categoryPersistencePort.existsById(id);
    }
} 