package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.exception.DomainException;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.spi.IDishPersistencePort;
import com.pragma.plazoleta.domain.api.IDishServicePort;
import com.pragma.plazoleta.domain.spi.ISecurityContextPort;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazoleta.domain.spi.ICategoryPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DishUseCase implements IDishServicePort {
    private final IDishPersistencePort dishPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final ICategoryPersistencePort categoryPersistencePort;
    private final ISecurityContextPort securityContextPort;

    @Override
    public Dish createDish(Dish dish, UUID restaurantOwnerId) {
        validateRequiredFields(dish);
        validateOwner(restaurantOwnerId);
        validateUniqueNameByRestaurant(dish.getName(), dish.getRestaurantId());
        dish.setActive(true);
        return dishPersistencePort.save(dish);
    }

    @Override
    public Dish getById(UUID id) {
        return dishPersistencePort.getById(id)
                .orElseThrow(() -> new DomainException("Dish not found"));
    }

    @Override
    public Dish updateDish(Dish dish, UUID restaurantOwnerId, Optional<Integer> price, Optional<String> description) {
        validateOwner(restaurantOwnerId);
        if (!price.isPresent() && !description.isPresent()) {
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
        
        return dishPersistencePort.updateDish(dish);
    }

    @Override
    public Dish updateDishActive(Dish dish, UUID restaurantOwnerId, Optional<Boolean> active) {
        validateOwner(restaurantOwnerId);
        if (!active.isPresent()) {
            throw new DomainException("Active field must be provided");
        }
        active.ifPresent(dish::setActive);
        return dishPersistencePort.updateDishActive(dish);
    }

    @Override
    public Page<Dish> getDishesByRestaurant(UUID restaurantId, Optional<Integer> categoryId, Pageable pageable) {
        if (!restaurantPersistencePort.existsById(restaurantId)) {
            throw new DomainException("Restaurant not found");
        }
        if (categoryId.isPresent() && !categoryPersistencePort.existsById(categoryId.get())) {
            throw new DomainException("Category not found");
        }
        
        return dishPersistencePort.getDishesByRestaurant(restaurantId, categoryId, pageable);
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