package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.Dish;

public interface IDishPersistencePort {
    Dish save(Dish dish);
} 