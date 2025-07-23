package com.pragma.plazoleta.infrastructure.output.jpa.adapter;

import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.spi.IDishPersistencePort;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.DishEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.mapper.IDishEntityMapper;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.IDishRepository;
import lombok.RequiredArgsConstructor;
import com.pragma.plazoleta.domain.exception.DomainException;

@RequiredArgsConstructor
public class DishJpaAdapter implements IDishPersistencePort {
    private final IDishRepository repository;
    private final IDishEntityMapper mapper;

    @Override
    public Dish save(Dish dish) {
        DishEntity entity = mapper.toEntity(dish);
        DishEntity saved = repository.save(entity);
        return mapper.toModel(saved);
    }

    @Override
    public Dish getById(String id) {
        return repository.findById(id)
                .map(mapper::toModel)
                .orElseThrow(() -> new DomainException("Dish not found"));
    }

    @Override
    public Dish updateDish(Dish dish, Integer price, String description) {
        DishEntity entity = repository.findById(dish.getId())
                .orElseThrow(() -> new DomainException("Dish not found"));
        if (price != null) {
            entity.setPrice(price);
        }
        if (description != null) {
            entity.setDescription(description);
        }
        DishEntity saved = repository.save(entity);
        return mapper.toModel(saved);
    }

    @Override
    public boolean existsByNameAndRestaurantId(String name, String restaurantId) {
        return repository.existsByNameAndRestaurantId(name, restaurantId);
    }
} 