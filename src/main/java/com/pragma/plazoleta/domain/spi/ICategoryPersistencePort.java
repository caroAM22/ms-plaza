package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.Category;
import java.util.List;
import java.util.Optional;

public interface ICategoryPersistencePort {
    Optional<Category> getByName(String name);
    Optional<Category> getById(Integer id);
    boolean existsById(Integer id);
    List<Category> getAll();
} 