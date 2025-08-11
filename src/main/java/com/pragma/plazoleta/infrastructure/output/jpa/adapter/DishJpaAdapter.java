package com.pragma.plazoleta.infrastructure.output.jpa.adapter;

import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.DomainPage;
import com.pragma.plazoleta.domain.spi.IDishPersistencePort;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.DishEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.mapper.IDishEntityMapper;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.IDishRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public boolean updateDish(Dish dish) {
        DishEntity entity = mapper.toDishEntity(dish);
        int updatedRows = repository.updatePriceAndDescription(entity.getId(), entity.getPrice(), entity.getDescription());
        return updatedRows != 0;
    }

    @Override
    @Transactional
    public boolean updateDishActive(Dish dish) {
        DishEntity entity = mapper.toDishEntity(dish);
        int updatedRows = repository.updateActive(entity.getId(), entity.isActive());
        return updatedRows != 0;
    }

    @Override
    public boolean existsByNameAndRestaurantId(String name, UUID restaurantId) {
        return repository.existsByNameAndRestaurantId(name, restaurantId.toString());
    }

    @Override
    public DomainPage<Dish> getDishesByRestaurant(UUID restaurantId, Optional<Integer> categoryId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<DishEntity> dishEntities;
        if (categoryId.isPresent()) {
            dishEntities = repository.findByRestaurantIdAndCategoryIdAndActiveIsTrue(
                restaurantId.toString(), categoryId.get(), pageRequest);
        } else {
            dishEntities = repository.findByRestaurantIdAndActiveIsTrue(
                restaurantId.toString(), pageRequest);
        }
        
        return DomainPage.<Dish>builder()
            .content(dishEntities.getContent().stream()
                .map(mapper::toDish)
                .toList())
            .pageNumber(dishEntities.getNumber())
            .pageSize(dishEntities.getSize())
            .totalElements(dishEntities.getTotalElements())
            .build();
    }

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id.toString());
    }
} 