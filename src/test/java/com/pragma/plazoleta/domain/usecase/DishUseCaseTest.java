package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.exception.DomainException;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.IDishPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DishUseCaseTest {
    private IDishPersistencePort dishPersistencePort;
    private DishUseCase useCase;

    @BeforeEach
    void setUp() {
        dishPersistencePort = Mockito.mock(IDishPersistencePort.class);
        useCase = new DishUseCase(dishPersistencePort);
    }

    @Test
    void createDish() {
        Dish dish = new Dish("id", "Pasta", 100, "desc", "img", 1, "rest1", true);
        Restaurant restaurant = new Restaurant();
        restaurant.setOwnerId("owner1");
        when(dishPersistencePort.save(dish)).thenReturn(dish);

        Dish result = useCase.createDish("owner1", dish, restaurant);
        
        assertEquals("Pasta", result.getName());
        verify(dishPersistencePort).save(dish);
    }

    @Test
    void createDishInvalidPrice() {
        Dish dish = new Dish("id", "Pasta", 0, "desc", "img", 1, "rest1", true);
        Restaurant restaurant = new Restaurant();
        restaurant.setOwnerId("owner1");
        DomainException ex = assertThrows(DomainException.class, () -> useCase.createDish("owner1", dish, restaurant));
        assertEquals("Dish price must be a positive integer greater than zero", ex.getMessage());
    }

    @Test
    void createDishEmptyName() {
        Dish dish = new Dish("id", "", 100, "desc", "img", 1, "rest1", true);
        Restaurant restaurant = new Restaurant();
        restaurant.setOwnerId("owner1");
        DomainException ex = assertThrows(DomainException.class, () -> useCase.createDish("owner1", dish, restaurant));
        assertEquals("Dish name is required and cannot be empty", ex.getMessage());
    }

    @Test
    void createDishEmptyDescription() {
        Dish dish = new Dish("id", "Pasta", 100, "", "img", 1, "rest1", true);
        Restaurant restaurant = new Restaurant();
        restaurant.setOwnerId("owner1");
        DomainException ex = assertThrows(DomainException.class, () -> useCase.createDish("owner1", dish, restaurant));
        assertEquals("Dish description is required and cannot be empty", ex.getMessage());
    }

    @Test
    void createDishEmptyImage() {
        Dish dish = new Dish("id", "Pasta", 100, "desc", "", 1, "rest1", true);
        Restaurant restaurant = new Restaurant();
        restaurant.setOwnerId("owner1");
        DomainException ex = assertThrows(DomainException.class, () -> useCase.createDish("owner1", dish, restaurant));
        assertEquals("Dish imageUrl is required and cannot be empty", ex.getMessage());
    }

    @Test
    void createDishInvalidCategory() {
        Dish dish = new Dish("id", "Pasta", 100, "desc", "img", 0, "rest1", true);
        Restaurant restaurant = new Restaurant();
        restaurant.setOwnerId("owner1");
        DomainException ex = assertThrows(DomainException.class, () -> useCase.createDish("owner1", dish, restaurant));
        assertEquals("Category not found", ex.getMessage());
    }

    @Test
    void createDishEmptyRestaurant() {
        Dish dish = new Dish("id", "Pasta", 100, "desc", "img", 1, "", true);
        Restaurant restaurant = new Restaurant();
        restaurant.setOwnerId("owner1");
        DomainException ex = assertThrows(DomainException.class, () -> useCase.createDish("owner1", dish, restaurant));
        assertEquals("Dish restaurantId is required and cannot be empty", ex.getMessage());
    }

    @Test
    void createDishNotOwner() {
        Dish dish = new Dish("id", "Pasta", 100, "desc", "img", 1, "rest1", true);
        Restaurant restaurant = new Restaurant();
        restaurant.setOwnerId("owner1");
        DomainException ex = assertThrows(DomainException.class, () -> useCase.createDish("otheruser", dish, restaurant));
        assertEquals("Only the restaurant owner can create dishes", ex.getMessage());
    }
} 