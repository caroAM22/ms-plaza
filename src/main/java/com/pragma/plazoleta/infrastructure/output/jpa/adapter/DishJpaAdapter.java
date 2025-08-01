package com.pragma.plazoleta.infrastructure.output.jpa.adapter;

import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.spi.IDishPersistencePort;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.DishEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.mapper.IDishEntityMapper;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.IDishRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class DishJpaAdapter implements IDishPersistencePort {
    private final IDishRepository repository;
    private final IDishEntityMapper mapper;

    @Override
    public Dish save(Dish dish) {
        DishEntity entity = mapper.toDishEntity(dish);
        DishEntity saved = repository.save(entity);
        return mapper.toDish(saved);
    }

    @Override
    public Optional<Dish> getById(UUID id) {
        return repository.findById(id.toString()).map(mapper::toDish);
    }

    @Override
    @Transactional
    public Optional<Dish> updateDish(Dish dish) {
        DishEntity entity = mapper.toDishEntity(dish);
        int updatedRows = repository.updatePriceAndDescription(entity.getId(), entity.getPrice(), entity.getDescription());
        if (updatedRows == 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(repository.findById(entity.getId())
                .map(mapper::toDish).orElse(null));
    }

    @Override
    @Transactional
    public Optional<Dish> updateDishActive(Dish dish) {
        DishEntity entity = mapper.toDishEntity(dish);
        int updatedRows = repository.updateActive(entity.getId(), entity.isActive());
        if (updatedRows == 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(repository.findById(entity.getId())
                .map(mapper::toDish).orElse(null));
    }

    @Override
    public boolean existsByNameAndRestaurantId(String name, UUID restaurantId) {
        return repository.existsByNameAndRestaurantId(name, restaurantId.toString());
    }

    @Override
    public Page<Dish> getDishesByRestaurant(UUID restaurantId, Optional<Integer> categoryId, Pageable pageable) {
        Page<DishEntity> dishEntities;
        if (categoryId.isPresent()) {
            dishEntities = repository.findByRestaurantIdAndCategoryIdAndActiveIsTrue(
                restaurantId.toString(), categoryId.get(), pageable);
        } else {
            dishEntities = repository.findByRestaurantIdAndActiveIsTrue(
                restaurantId.toString(), pageable);
        }
        return dishEntities.map(mapper::toDish);
    }

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id.toString());
    }
} 