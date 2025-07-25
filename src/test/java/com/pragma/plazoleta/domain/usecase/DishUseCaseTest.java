package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.application.dto.request.DishUpdateRequest;
import com.pragma.plazoleta.domain.exception.DomainException;
import com.pragma.plazoleta.domain.model.Dish;
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
        String restaurantOwnerId = "owner1";
        when(dishPersistencePort.save(dish)).thenReturn(dish);
        Dish result = useCase.createDish("owner1", "OWNER", dish, restaurantOwnerId);
        assertEquals("Pasta", result.getName());
        verify(dishPersistencePort).save(dish);
    }

    @Test
    void createDishInvalidPrice() {
        Dish dish = new Dish("id", "Pasta", 0, "desc", "img", 1, "rest1", true);
        String restaurantOwnerId = "owner1";
        DomainException ex = assertThrows(DomainException.class, () -> useCase.createDish("owner1", "OWNER", dish, restaurantOwnerId));
        assertEquals("Dish price must be a positive integer greater than zero", ex.getMessage());
    }

    @Test
    void createDishEmptyName() {
        Dish dish = new Dish("id", "", 100, "desc", "img", 1, "rest1", true);
        String restaurantOwnerId = "owner1";
        DomainException ex = assertThrows(DomainException.class, () -> useCase.createDish("owner1", "OWNER", dish, restaurantOwnerId));
        assertEquals("Dish name is required and cannot be empty", ex.getMessage());
    }

    @Test
    void createDishEmptyDescription() {
        Dish dish = new Dish("id", "Pasta", 100, "", "img", 1, "rest1", true);
        String restaurantOwnerId = "owner1";
        DomainException ex = assertThrows(DomainException.class, () -> useCase.createDish("owner1", "OWNER", dish, restaurantOwnerId));
        assertEquals("Dish description is required and cannot be empty", ex.getMessage());
    }

    @Test
    void createDishEmptyImage() {
        Dish dish = new Dish("id", "Pasta", 100, "desc", "", 1, "rest1", true);
        String restaurantOwnerId = "owner1";
        DomainException ex = assertThrows(DomainException.class, () -> useCase.createDish("owner1", "OWNER", dish, restaurantOwnerId));
        assertEquals("Dish imageUrl is required and cannot be empty", ex.getMessage());
    }

    @Test
    void createDishInvalidCategory() {
        Dish dish = new Dish("id", "Pasta", 100, "desc", "img", 0, "rest1", true);
        String restaurantOwnerId = "owner1";
        DomainException ex = assertThrows(DomainException.class, () -> useCase.createDish("owner1", "OWNER", dish, restaurantOwnerId));
        assertEquals("Category not found", ex.getMessage());
    }

    @Test
    void createDishEmptyRestaurant() {
        Dish dish = new Dish("id", "Pasta", 100, "desc", "img", 1, "", true);
        String restaurantOwnerId = "owner1";
        DomainException ex = assertThrows(DomainException.class, () -> useCase.createDish("owner1", "OWNER", dish, restaurantOwnerId));
        assertEquals("Dish restaurantId is required and cannot be empty", ex.getMessage());
    }

    @Test
    void createDishNotOwner() {
        Dish dish = new Dish("id", "Pasta", 100, "desc", "img", 1, "rest1", true);
        String restaurantOwnerId = "owner1";
        DomainException ex = assertThrows(DomainException.class, () -> useCase.createDish("otheruser", "OWNER", dish, restaurantOwnerId));
        assertEquals("Only the restaurant owner can create or update dishes", ex.getMessage());
    }

    @Test
    void updateDishInvalidPriceMinusOne() {
        Dish dish = new Dish("id", "Pasta", 100, "desc", "img", 1, "rest1", true);
        DishUpdateRequest dto = new DishUpdateRequest();
        dto.setPrice(-1);
        Integer price = dto.getPrice();
        String description = dish.getDescription();
        String ownerId = "owner1";
        String userId = "owner1";
        String role = "OWNER";
        DomainException ex = assertThrows(DomainException.class, () -> useCase.updateDish(dish, ownerId, userId, role, price, description));
        assertEquals("Dish price must be a positive integer greater than zero", ex.getMessage());
    }

    @Test
    void updateDishInvalidPriceZero() {
        Dish dish = new Dish("id", "Pasta", 100, "desc", "img", 1, "rest1", true);
        DishUpdateRequest dto = new DishUpdateRequest();
        dto.setPrice(0);
        Integer price = dto.getPrice();
        String description = dish.getDescription();
        String ownerId = "owner1";
        String userId = "owner1";
        String role = "OWNER";
        DomainException ex = assertThrows(DomainException.class, () -> useCase.updateDish(dish, ownerId, userId, role, price, description));
        assertEquals("Dish price must be a positive integer greater than zero", ex.getMessage());
    }

    @Test
    void updateDishOnlyPrice() {
        Dish dish = new Dish("id", "Pasta", 100, "desc", "img", 1, "rest1", true);
        DishUpdateRequest dto = new DishUpdateRequest();
        dto.setPrice(200);
        when(dishPersistencePort.updateDish(any(Dish.class), eq(200), any()))
            .thenReturn(new Dish("id", "Pasta", 200, "desc", "img", 1, "rest1", true));
        String ownerId = "owner1";
        String userId = "owner1";
        String role = "OWNER";
        Dish result = useCase.updateDish(dish, ownerId, userId, role, dto.getPrice(), dto.getDescription());
        assertEquals(200, result.getPrice());
    }

    @Test
    void updateDishOnlyDescription() {
        Dish dish = new Dish("id", "Pasta", 100, "desc", "img", 1, "rest1", true);
        DishUpdateRequest dto = new DishUpdateRequest();
        dto.setDescription("Nueva descripcion");
        when(dishPersistencePort.updateDish(any(Dish.class), any(), eq("Nueva descripcion")))
            .thenReturn(new Dish("id", "Pasta", 100, "Nueva descripcion", "img", 1, "rest1", true));
        String ownerId = "owner1";
        String userId = "owner1";
        String role = "OWNER";
        Dish result = useCase.updateDish(dish, ownerId, userId, role, dto.getPrice(), dto.getDescription());
        assertEquals("Nueva descripcion", result.getDescription());
    }

    @Test
    void updateInvalidDishPriceAndDescription() {
        Dish dish = new Dish("id", "Pasta", 100, "desc", "img", 1, "rest1", true);
        DishUpdateRequest dto = new DishUpdateRequest();
        Integer price = dto.getPrice();
        String description = dto.getDescription();
        String ownerId = "owner1";
        String userId = "owner1";
        String role = "OWNER";
        DomainException ex = assertThrows(DomainException.class, () -> useCase.updateDish(dish, ownerId, userId, role, price, description));
        assertEquals("At least one field (price or description) must be provided", ex.getMessage(), "El mensaje de excepción no coincide");
    }

    @Test
    void updateDishPriceAndDescription() {
        Dish dish = new Dish("id", "Pasta", 100, "desc", "img", 1, "rest1", true);
        DishUpdateRequest dto = new DishUpdateRequest();
        dto.setPrice(300);
        dto.setDescription("Otra descripcion");
        when(dishPersistencePort.updateDish(any(Dish.class), eq(300), eq("Otra descripcion"))).thenReturn(new Dish("id", "Pasta", 300, "Otra descripcion", "img", 1, "rest1", true));
        String ownerId = "owner1";
        String userId = "owner1";
        String role = "OWNER";
        Dish result = useCase.updateDish(dish, ownerId, userId, role, dto.getPrice(), dto.getDescription());
        assertEquals(300, result.getPrice());
        assertEquals("Otra descripcion", result.getDescription());
    }

    @Test
    void updateDishWithEmptyDescription() {
        Dish dish = new Dish("id", "Pasta", 100, "desc", "img", 1, "rest1", true);
        DishUpdateRequest dto = new DishUpdateRequest();
        dto.setDescription("");
        Integer price = dto.getPrice();
        String description = dto.getDescription();
        String ownerId = "owner1";
        String userId = "owner1";
        String role = "OWNER";
        DomainException ex = assertThrows(DomainException.class, () -> useCase.updateDish(dish, ownerId, userId, role, price, description));
        assertEquals("Dish description is required and cannot be empty", ex.getMessage());
    }

    @Test
    void createDishWithDuplicateName() {
        Dish dish = new Dish("id", "Pasta", 100, "desc", "img", 1, "rest1", true);
        String restaurantOwnerId = "owner1";
        when(dishPersistencePort.existsByNameAndRestaurantId("Pasta", "rest1")).thenReturn(true);
        DomainException ex = assertThrows(DomainException.class, () -> useCase.createDish("owner1", "OWNER", dish, restaurantOwnerId));
        assertEquals("A dish with this name already exists in this restaurant", ex.getMessage());
    }

    @Test
    void updateDishActiveToTrue() {
        Dish dish = new Dish("id", "Pasta", 100, "desc", "img", 1, "rest1", false);
        Dish updatedDish = new Dish("id", "Pasta", 100, "desc", "img", 1, "rest1", true);
        String ownerId = "owner1";
        String userId = "owner1";
        String role = "OWNER";
        when(dishPersistencePort.updateDishActive(any(Dish.class))).thenReturn(updatedDish);
        Dish result = useCase.updateDishActive(dish, ownerId, userId, role, true);
        assertTrue(result.isActive());
        verify(dishPersistencePort).updateDishActive(any(Dish.class));
    }

    @Test
    void updateDishActiveToFalse() {
        Dish dish = new Dish("id", "Pasta", 100, "desc", "img", 1, "rest1", true);
        Dish updatedDish = new Dish("id", "Pasta", 100, "desc", "img", 1, "rest1", false);
        String ownerId = "owner1";
        String userId = "owner1";
        String role = "OWNER";
        when(dishPersistencePort.updateDishActive(any(Dish.class))).thenReturn(updatedDish);
        Dish result = useCase.updateDishActive(dish, ownerId, userId, role, false);
        assertFalse(result.isActive());
        verify(dishPersistencePort).updateDishActive(any(Dish.class));
    }

    @Test
    void updateDishActiveThrowsIfNotOwner() {
        Dish dish = new Dish("id", "Pasta", 100, "desc", "img", 1, "rest1", true);
        String ownerId = "owner1";
        String userId = "otheruser";
        String role = "OWNER";
        DomainException ex = assertThrows(DomainException.class, () -> useCase.updateDishActive(dish, ownerId, userId, role, false));
        assertEquals("Only the restaurant owner can create or update dishes", ex.getMessage());
    }
} 