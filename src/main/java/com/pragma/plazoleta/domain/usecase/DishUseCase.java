package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.exception.DomainException;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.IDishPersistencePort;

public class DishUseCase {
    private IDishPersistencePort dishPersistencePort;

    public DishUseCase(IDishPersistencePort dishPersistencePort) {
        this.dishPersistencePort = dishPersistencePort;
    }

    public Dish createDish(String userId, Dish dish, Restaurant restaurantOwner) {
        validateRequiredFields(dish);
        validateOwner(userId, restaurantOwner);
        dish.setActive(true);
        return dishPersistencePort.save(dish);
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

    private void validateOwner(String userId, Restaurant restaurant) {
        if (!userId.equals(restaurant.getOwnerId())) {
            throw new DomainException("Only the restaurant owner can create dishes");
        }
    }
} 