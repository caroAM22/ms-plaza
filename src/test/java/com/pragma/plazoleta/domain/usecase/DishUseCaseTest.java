package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.exception.DomainException;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.spi.IDishPersistencePort;
import com.pragma.plazoleta.domain.spi.ISecurityContextPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DishUseCaseTest {
    private static final UUID DISH_ID = UUID.randomUUID();
    private static final UUID RESTAURANT_ID = UUID.randomUUID();
    private static final Integer CATEGORY_ID = 1;
    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final UUID OTHER_USER_ID = UUID.randomUUID();
    
    private IDishPersistencePort dishPersistencePort;
    private ISecurityContextPort securityContextPort;
    private DishUseCase useCase;

    @BeforeEach
    void setUp() {
        dishPersistencePort = Mockito.mock(IDishPersistencePort.class);
        securityContextPort = Mockito.mock(ISecurityContextPort.class);
        useCase = new DishUseCase(dishPersistencePort, securityContextPort);
    }

    @Test
    void createDishSuccess() {
        Dish dish = new Dish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID, RESTAURANT_ID, true);
        
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(OWNER_ID);
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("OWNER");
        when(dishPersistencePort.existsByNameAndRestaurantId("Pasta", RESTAURANT_ID)).thenReturn(false);
        when(dishPersistencePort.save(dish)).thenReturn(dish);
        Dish result = useCase.createDish(dish, OWNER_ID);
        
        assertEquals("Pasta", result.getName());
        verify(dishPersistencePort).save(dish);
    }

    @Test
    void createDishInvalidPrice() {
        Dish dish = new Dish(DISH_ID, "Pasta", 0, "desc", "img", CATEGORY_ID, RESTAURANT_ID, true);
        
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(OWNER_ID);
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("OWNER");

        DomainException ex = assertThrows(DomainException.class, () -> useCase.createDish(dish, OWNER_ID));
        assertEquals("Dish price must be a positive integer", ex.getMessage());
    }

    @Test
    void createDishEmptyName() {
        Dish dish = new Dish(DISH_ID, "", 100, "desc", "img", CATEGORY_ID, RESTAURANT_ID, true);
        
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(OWNER_ID);
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("OWNER");
        
        DomainException ex = assertThrows(DomainException.class, () -> useCase.createDish(dish, OWNER_ID));
        assertEquals("Dish name is required", ex.getMessage());
    }

    @Test
    void createDishEmptyDescription() {
        Dish dish = new Dish(DISH_ID, "Pasta", 100, "", "img", CATEGORY_ID, RESTAURANT_ID, true);
        
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(OWNER_ID);
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("OWNER");
        
        DomainException ex = assertThrows(DomainException.class, () -> useCase.createDish(dish, OWNER_ID));
        assertEquals("Dish description is required", ex.getMessage());
    }

    @Test
    void createDishEmptyImageUrl() {
        Dish dish = new Dish(DISH_ID, "Pasta", 100, "desc", "", CATEGORY_ID, RESTAURANT_ID, true);
        
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(OWNER_ID);
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("OWNER");
        
        DomainException ex = assertThrows(DomainException.class, () -> useCase.createDish(dish, OWNER_ID));
        assertEquals("Dish imageUrl is required", ex.getMessage());
    }

    @Test
    void createDishEmptyRestaurantId() {
        Dish dish = new Dish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID, null, true);
        
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(OWNER_ID);
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("OWNER");
        
        DomainException ex = assertThrows(DomainException.class, () -> {
            useCase.createDish(dish, OWNER_ID);
        });
        assertEquals("Dish restaurantId is required", ex.getMessage());
    }

    @Test
    void createDishNotOwner() {
        Dish dish = new Dish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID, RESTAURANT_ID, true);
        
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(OTHER_USER_ID);
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("OWNER");
        
        DomainException ex = assertThrows(DomainException.class, () -> {
            useCase.createDish(dish, OWNER_ID);
        });
        assertEquals("Only the restaurant owner can create or update dishes", ex.getMessage());
    }

    @Test
    void updateDishOnlyPrice() {
        Dish dish = new Dish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID, RESTAURANT_ID, true);
        Dish updatedDish = new Dish(DISH_ID, "Pasta", 200, "desc", "img", CATEGORY_ID, RESTAURANT_ID, true);
        
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(OWNER_ID);
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("OWNER");
        when(dishPersistencePort.updateDish(dish)).thenReturn(updatedDish);
        
        Dish result = useCase.updateDish(dish, OWNER_ID, Optional.of(200), Optional.empty());
        
        assertEquals(200, result.getPrice());
        verify(dishPersistencePort).updateDish(dish);
    }

    @Test
    void updateDishOnlyDescription() {
        Dish dish = new Dish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID, RESTAURANT_ID, true);
        Dish updatedDish = new Dish(DISH_ID, "Pasta", 100, "Nueva descripcion", "img", CATEGORY_ID, RESTAURANT_ID, true);
        
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(OWNER_ID);
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("OWNER");
        when(dishPersistencePort.updateDish(dish)).thenReturn(updatedDish);
        
        Dish result = useCase.updateDish(dish, OWNER_ID, Optional.empty(), Optional.of("Nueva descripcion"));
        
        assertEquals("Nueva descripcion", result.getDescription());
        verify(dishPersistencePort).updateDish(dish);
    }

    @Test
    void updateInvalidDishPriceAndDescription() {
        Dish dish = new Dish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID, RESTAURANT_ID, true);
        
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(OWNER_ID);
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("OWNER");
        
        DomainException ex = assertThrows(DomainException.class, () -> useCase.updateDish(dish, OWNER_ID, Optional.empty(), Optional.empty()));
        assertEquals("At least one field (price or description) must be provided", ex.getMessage());
    }

    @Test
    void updateDishPriceAndDescription() {
        Dish dish = new Dish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID, RESTAURANT_ID, true);
        Dish updatedDish = new Dish(DISH_ID, "Pasta", 300, "Otra descripcion", "img", CATEGORY_ID, RESTAURANT_ID, true);
        
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(OWNER_ID);
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("OWNER");
        when(dishPersistencePort.updateDish(dish)).thenReturn(updatedDish);
        
        Dish result = useCase.updateDish(dish, OWNER_ID, Optional.of(300), Optional.of("Otra descripcion"));
        
        assertEquals(300, result.getPrice());
        assertEquals("Otra descripcion", result.getDescription());
        verify(dishPersistencePort).updateDish(dish);
    }

    @Test
    void updateDishWithEmptyDescription() {
        Dish dish = new Dish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID, RESTAURANT_ID, true);
        
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(OWNER_ID);
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("OWNER");
        
        DomainException ex = assertThrows(DomainException.class, () -> useCase.updateDish(dish, OWNER_ID, Optional.empty(), Optional.of("")));
        assertEquals("Dish description cannot be empty", ex.getMessage());
    }

    @Test
    void updateDishWithNegativePrice() {
        Dish dish = new Dish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID, RESTAURANT_ID, true);
        
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(OWNER_ID);
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("OWNER");
        
        DomainException ex = assertThrows(DomainException.class, () -> {
            useCase.updateDish(dish, OWNER_ID, Optional.of(-2), Optional.empty());
        });
        assertEquals("Dish price must be a positive integer", ex.getMessage());
    }

    @Test
    void createDishWithDuplicateName() {
        Dish dish = new Dish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID, RESTAURANT_ID, true);
        
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(OWNER_ID);
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("OWNER");
        when(dishPersistencePort.existsByNameAndRestaurantId("Pasta", RESTAURANT_ID)).thenReturn(true);
        
        DomainException ex = assertThrows(DomainException.class, () -> {
            useCase.createDish(dish, OWNER_ID);
        });
        assertEquals("A dish with this name already exists in this restaurant", ex.getMessage());
    }

    @Test
    void updateDishActiveToTrue() {
        Dish dish = new Dish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID, RESTAURANT_ID, false);
        Dish updatedDish = new Dish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID, RESTAURANT_ID, true);
        
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(OWNER_ID);
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("OWNER");
        when(dishPersistencePort.updateDishActive(dish)).thenReturn(updatedDish);
        
        Dish result = useCase.updateDishActive(dish, OWNER_ID, Optional.of(true));
        
        assertTrue(result.isActive());
        verify(dishPersistencePort).updateDishActive(dish);
    }

    @Test
    void updateDishActiveToFalse() {
        Dish dish = new Dish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID, RESTAURANT_ID, true);
        Dish updatedDish = new Dish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID, RESTAURANT_ID, false);
        
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(OWNER_ID);
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("OWNER");
        when(dishPersistencePort.updateDishActive(dish)).thenReturn(updatedDish);
        
        Dish result = useCase.updateDishActive(dish, OWNER_ID, Optional.of(false));
        
        assertFalse(result.isActive());
        verify(dishPersistencePort).updateDishActive(dish);
    }

    @Test
    void updateDishActiveThrowsIfNotOwner() {
        Dish dish = new Dish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID, RESTAURANT_ID, true);
        
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(OTHER_USER_ID);
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("OWNER");
        
        DomainException ex = assertThrows(DomainException.class, () -> {
            useCase.updateDishActive(dish, OWNER_ID, Optional.of(false));
        });
        assertEquals("Only the restaurant owner can create or update dishes", ex.getMessage());
    }

    @Test
    void updateDishActiveThrowsIfActiveNotProvided() {
        Dish dish = new Dish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID, RESTAURANT_ID, true);
        
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(OWNER_ID);
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("OWNER");
        DomainException ex = assertThrows(DomainException.class, () -> {
            useCase.updateDishActive(dish, OWNER_ID, Optional.empty());
        });
        assertEquals("Active field must be provided", ex.getMessage());
    }
} 