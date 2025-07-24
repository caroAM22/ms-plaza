package com.pragma.plazoleta.infrastructure.output.jpa.adapter;

import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.spi.ICategoryPersistencePort;
import com.pragma.plazoleta.infrastructure.output.jpa.mapper.ICategoryEntityMapper;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.ICategoryRepository;
import lombok.RequiredArgsConstructor;
import java.util.Optional;
import java.util.List;

@RequiredArgsConstructor
public class CategoryJpaAdapter implements ICategoryPersistencePort {
    private final ICategoryRepository repository;
    private final ICategoryEntityMapper mapper;

    @Override
    public List<Category> getAll() {
        return repository.findAll().stream()
                .map(mapper::toModel)
                .toList();
    }

    @Override
    public Optional<Category> getByName(String name) {
        return repository.findByName(name).map(mapper::toModel);
    }
} 