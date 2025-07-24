package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.exception.DomainException;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.spi.IDishPersistencePort;
import com.pragma.plazoleta.domain.api.IDishServicePort;

public class DishUseCase implements IDishServicePort {
    private IDishPersistencePort dishPersistencePort;

    public DishUseCase(IDishPersistencePort dishPersistencePort) {
        this.dishPersistencePort = dishPersistencePort;
    }

    @Override
    public Dish createDish(String userId, String role, Dish dish, String restaurantOwnerId) {
        validateRequiredFields(dish);
        validateOwner(userId, role, restaurantOwnerId);
        validateUniqueNameByRestaurant(dish.getName(), dish.getRestaurantId());
        dish.setActive(true);
        return dishPersistencePort.save(dish);
    }

    @Override
    public Dish getById(String id) {
        return dishPersistencePort.getById(id);
    }

    @Override
    public Dish updateDish(Dish dish, String restaurantOwnerId, String userId, String role, Integer price, String description) {
        validateOwner(userId, role, restaurantOwnerId);
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

    @Override
    public Dish updateDishActive(Dish dish, String restaurantOwnerId, String userId, String role, boolean active) {
        validateOwner(userId, role, restaurantOwnerId);
        dish.setActive(active);
        return dishPersistencePort.updateDishActive(dish);
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

    private void validateUniqueNameByRestaurant(String name, String restaurantId) {
        if (dishPersistencePort.existsByNameAndRestaurantId(name, restaurantId)) {
            throw new DomainException("A dish with this name already exists in this restaurant");
        }
    }

    private void validateOwner(String userId, String role, String restaurantOwnerId) {
        if (!"OWNER".equalsIgnoreCase(role) || !userId.equals(restaurantOwnerId)) {
            throw new DomainException("Only the restaurant owner can create or update dishes");
        }
    }

} 