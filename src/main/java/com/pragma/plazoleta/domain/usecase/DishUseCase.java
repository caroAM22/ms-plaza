package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.exception.DomainException;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.DomainPage;
import com.pragma.plazoleta.domain.spi.IDishPersistencePort;
import com.pragma.plazoleta.domain.api.IDishServicePort;
import com.pragma.plazoleta.domain.api.IRestaurantServicePort;
import com.pragma.plazoleta.domain.api.ICategoryServicePort;
import com.pragma.plazoleta.domain.spi.ISecurityContextPort;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class DishUseCase implements IDishServicePort {
    private final IDishPersistencePort dishPersistencePort;
    private final IRestaurantServicePort restaurantServicePort;
    private final ICategoryServicePort categoryServicePort;
    private final ISecurityContextPort securityContextPort;

    @Override
    public Dish createDish(Dish dish) {
        validateRequiredFields(dish);
        validateOwner(restaurantServicePort.getRestaurantById(dish.getRestaurantId()).getOwnerId());
        validateUniqueNameByRestaurant(dish.getName(), dish.getRestaurantId());
        dish.setActive(true);
        return dishPersistencePort.save(dish);
    }

    @Override
    public Dish updateDish(UUID dishId, Optional<Integer> price, Optional<String> description) {
        Dish dish = getById(dishId);
        validateOwner(restaurantServicePort.getRestaurantById(dish.getRestaurantId()).getOwnerId());
        if (price.isEmpty() && description.isEmpty()) {
            throw new DomainException("At least one field (price or description) must be provided");
        }
        if (price.isPresent()) {
            if (price.get() <= 0) throw new DomainException("Dish price must be a positive integer");
            dish.setPrice(price.get());
        }
        if (description.isPresent()) {
            if (isBlank(description.get())) throw new DomainException("Dish description cannot be empty");
            dish.setDescription(description.get());
        }
        boolean updatedDish = dishPersistencePort.updateDish(dish);
        if (!updatedDish) {
            throw new DomainException("Failed to update dish - dish not found after update");
        }
        return getById(dishId);
    }

    @Override
    public Dish updateDishActive(UUID dishId, Optional<Boolean> active) {
        Dish dish = getById(dishId);
        validateOwner(restaurantServicePort.getRestaurantById(dish.getRestaurantId()).getOwnerId());
        if (active.isEmpty()) {
            throw new DomainException("Active field must be provided");
        }
        active.ifPresent(dish::setActive);
        boolean updatedDish = dishPersistencePort.updateDishActive(dish);
        if (!updatedDish) {
            throw new DomainException("Failed to update dish - dish not found after update");
        }
        return getById(dishId);
    }

    @Override
    public DomainPage<Dish> getDishesByRestaurant(UUID restaurantId, Optional<Integer> categoryId, int page, int size) {
        if (!restaurantServicePort.existsById(restaurantId)) {
            throw new DomainException("Restaurant not found");
        }
        if (categoryId.isPresent() && !categoryServicePort.existsById(categoryId.get())) {
            throw new DomainException("Category not found");
        }
        
        return dishPersistencePort.getDishesByRestaurant(restaurantId, categoryId, page, size);
    }

    @Override
    public boolean existsById(UUID id) {
        return dishPersistencePort.existsById(id);
    }

    @Override
    public boolean isActiveById(UUID id) {
        return dishPersistencePort.getById(id)
                .map(Dish::isActive)
                .orElse(false);
    }

    private Dish getById(UUID id) {
        return dishPersistencePort.getById(id)
                .orElseThrow(() -> new DomainException("Dish not found"));
    }

    private void validateRequiredFields(Dish dish) {
        if (isBlank(dish.getName())) throw new DomainException("Dish name is required");
        if (dish.getPrice() <= 0) throw new DomainException("Dish price must be a positive integer");
        if (isBlank(dish.getDescription())) throw new DomainException("Dish description is required");
        if (isBlank(dish.getImageUrl())) throw new DomainException("Dish imageUrl is required");
        if (dish.getRestaurantId() == null) throw new DomainException("Dish restaurantId is required");
    }

    private void validateUniqueNameByRestaurant(String name, UUID restaurantId) {
        if (dishPersistencePort.existsByNameAndRestaurantId(name, restaurantId)) {
            throw new DomainException("A dish with this name already exists in this restaurant");
        }
    }

    private void validateOwner(UUID restaurantOwnerId) {
        UUID userId = securityContextPort.getUserIdOfUserAutenticated();
        String role = securityContextPort.getRoleOfUserAutenticated();
        if (!"OWNER".equalsIgnoreCase(role) || !userId.equals(restaurantOwnerId)) {
            throw new DomainException("Only the restaurant owner can create or update dishes");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
} 