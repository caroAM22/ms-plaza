package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.exception.DomainException;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.IDishPersistencePort;
import com.pragma.plazoleta.domain.api.IDishServicePort;

public class DishUseCase implements IDishServicePort {
    private IDishPersistencePort dishPersistencePort;

    public DishUseCase(IDishPersistencePort dishPersistencePort) {
        this.dishPersistencePort = dishPersistencePort;
    }

    @Override
    public Dish createDish(String userId, Dish dish, Restaurant restaurantOwner) {
        validateRequiredFields(dish);
        validateOwner(userId, restaurantOwner);
        validateUniqueName(dish.getName());
        dish.setActive(true);
        return dishPersistencePort.save(dish);
    }

    @Override
    public Dish getById(String id) {
        return dishPersistencePort.getById(id);
    }

    @Override
    public Dish updateDish(Dish dish, Integer price, String description) {
        if (price == null && description == null) {
            throw new DomainException("At least one field (price or description) must be provided");
        }
        if (price != null) {
            if (price <= 0) {
                throw new DomainException("Dish price must be a positive integer greater than zero");
            }
            dish.setPrice(price);
        }
        if (description != null) {
            if (description.trim().isEmpty()) {
                throw new DomainException("Dish description is required and cannot be empty");
            }
            dish.setDescription(description);
        }
        return dishPersistencePort.updateDish(dish, dish.getPrice(), dish.getDescription());
    }

    private void validateRequiredFields(Dish dish) {
        if (dish.getName() == null || dish.getName().trim().isEmpty()) {
            throw new DomainException("Dish name is required and cannot be empty");
        }
        if (dish.getPrice() <= 0) {
            throw new DomainException("Dish price must be a positive integer greater than zero");
        }
        if (dish.getDescription() == null || dish.getDescription().trim().isEmpty()) {
            throw new DomainException("Dish description is required and cannot be empty");
        }
        if (dish.getImageUrl() == null || dish.getImageUrl().trim().isEmpty()) {
            throw new DomainException("Dish imageUrl is required and cannot be empty");
        }
        if (dish.getCategoryId() <= 0) {
            throw new DomainException("Category not found");
        }
        if (dish.getRestaurantId() == null || dish.getRestaurantId().trim().isEmpty()) {
            throw new DomainException("Dish restaurantId is required and cannot be empty");
        }
    }

    private void validateUniqueName(String name) {
        if (dishPersistencePort.existsByName(name)) {
            throw new DomainException("A dish with this name already exists");
        }
    }

    private void validateOwner(String userId, Restaurant restaurant) {
        if (!userId.equals(restaurant.getOwnerId())) {
            throw new DomainException("Only the restaurant owner can create dishes");
        }
    }
} 