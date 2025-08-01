package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.exception.DomainException;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.IDishPersistencePort;
import com.pragma.plazoleta.domain.api.IRestaurantServicePort;
import com.pragma.plazoleta.domain.api.ICategoryServicePort;
import com.pragma.plazoleta.domain.spi.ISecurityContextPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DishUseCaseTest {
    // Constants
    private static final UUID DISH_ID = UUID.randomUUID();
    private static final UUID RESTAURANT_ID = UUID.randomUUID();
    private static final Integer CATEGORY_ID = 1;
    private static final Integer CATEGORY_ID_2 = 2;
    private static final Integer CATEGORY_ID_3 = 3;
    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final UUID OTHER_USER_ID = UUID.randomUUID();
    
    // Dish data
    private static final String DISH_NAME = "Hamburguesa Clásica";
    private static final String DISH_NAME_2 = "Pizza Margherita";
    private static final String DISH_NAME_3 = "Ensalada César";
    private static final Integer DISH_PRICE = 15000;
    private static final Integer DISH_PRICE_2 = 20000;
    private static final Integer DISH_PRICE_3 = 12000;
    private static final String DISH_DESCRIPTION = "Hamburguesa con carne, lechuga, tomate y queso";
    private static final String DISH_DESCRIPTION_2 = "Pizza con tomate y mozzarella";
    private static final String DISH_DESCRIPTION_3 = "Ensalada con lechuga y aderezo";
    private static final String DISH_IMAGE_URL = "http://example.com/hamburger.jpg";
    private static final String DISH_IMAGE_URL_2 = "http://example.com/pizza.jpg";
    private static final String DISH_IMAGE_URL_3 = "http://example.com/salad.jpg";
    
    // Mocks
    private IDishPersistencePort dishPersistencePort;
    private IRestaurantServicePort restaurantServicePort;
    private ICategoryServicePort categoryServicePort;
    private ISecurityContextPort securityContextPort;
    private DishUseCase useCase;
    
    // Test data
    private Dish defaultDish;
    private Dish dish2;
    private Dish dish3;
    private PageRequest defaultPageRequest;
    private Restaurant mockRestaurant;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        dishPersistencePort = Mockito.mock(IDishPersistencePort.class);
        restaurantServicePort = Mockito.mock(IRestaurantServicePort.class);
        categoryServicePort = Mockito.mock(ICategoryServicePort.class);
        securityContextPort = Mockito.mock(ISecurityContextPort.class);
        useCase = new DishUseCase(dishPersistencePort, restaurantServicePort, categoryServicePort, securityContextPort);
        
        // Initialize test data
        defaultDish = createDish(DISH_ID, DISH_NAME, DISH_PRICE, DISH_DESCRIPTION, DISH_IMAGE_URL, CATEGORY_ID);
        dish2 = createDish(UUID.randomUUID(), DISH_NAME_2, DISH_PRICE_2, DISH_DESCRIPTION_2, DISH_IMAGE_URL_2, CATEGORY_ID_2);
        dish3 = createDish(UUID.randomUUID(), DISH_NAME_3, DISH_PRICE_3, DISH_DESCRIPTION_3, DISH_IMAGE_URL_3, CATEGORY_ID_3);
        defaultPageRequest = PageRequest.of(0, 10, Sort.by("name").ascending());
        mockRestaurant = new Restaurant();
        mockRestaurant.setOwnerId(OWNER_ID);
    }
    
    private Dish createDish(UUID id, String name, Integer price, String description, String imageUrl, Integer categoryId) {
        return new Dish(id, name, price, description, imageUrl, categoryId, RESTAURANT_ID, true);
    }
    
    private void setupOwnerAuthentication() {
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(OWNER_ID);
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("OWNER");
    }
    
    private void setupOtherUserAuthentication() {
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(OTHER_USER_ID);
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("OWNER");
    }

    @Test
    void existsByIdReturnsTrueIfDishFound() {
        when(dishPersistencePort.existsById(DISH_ID)).thenReturn(true);
        
        boolean result = useCase.existsById(DISH_ID);
        assertTrue(result);
    }

    @Test
    void existsByIdReturnsFalseIfDishFound() {
        when(dishPersistencePort.existsById(DISH_ID)).thenReturn(false);
        
        boolean result = useCase.existsById(DISH_ID);
        assertFalse(result);
    }

    @Test
    void isActiveByIdReturnsTrueIfDishFound() {
        when(dishPersistencePort.getById(DISH_ID)).thenReturn(Optional.of(defaultDish));
        
        boolean result = useCase.isActiveById(DISH_ID);
        assertTrue(result);
    }

    @Test
    void isActiveByIdReturnsFalseIfDishFound() {
        when(dishPersistencePort.getById(DISH_ID)).thenReturn(Optional.empty());
        
        boolean result = useCase.isActiveById(DISH_ID);
        assertFalse(result);
    }

    @Test
    void createDishSuccess() {
        Dish dish = createDish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID);
        
        setupOwnerAuthentication();
        when(dishPersistencePort.existsByNameAndRestaurantId("Pasta", RESTAURANT_ID)).thenReturn(false);
        when(dishPersistencePort.save(dish)).thenReturn(dish);
        when(restaurantServicePort.getRestaurantById(dish.getRestaurantId())).thenReturn(mockRestaurant);
        Dish result = useCase.createDish(dish);
        
        assertEquals("Pasta", result.getName());
        verify(dishPersistencePort).save(dish);
    }

    @Test
    void createDishInvalidPrice() {
        Dish dish = createDish(DISH_ID, "Pasta", 0, "desc", "img", CATEGORY_ID);
        
        setupOwnerAuthentication();

        DomainException ex = assertThrows(DomainException.class, () -> useCase.createDish(dish));
        assertEquals("Dish price must be a positive integer", ex.getMessage());
    }

    @Test
    void createDishEmptyName() {
        Dish dish = createDish(DISH_ID, "", 100, "desc", "img", CATEGORY_ID);
        
        setupOwnerAuthentication();
        
        DomainException ex = assertThrows(DomainException.class, () -> useCase.createDish(dish));
        assertEquals("Dish name is required", ex.getMessage());
    }

    @Test
    void createDishEmptyDescription() {
        Dish dish = createDish(DISH_ID, "Pasta", 100, "", "img", CATEGORY_ID);
        
        setupOwnerAuthentication();
        
        DomainException ex = assertThrows(DomainException.class, () -> useCase.createDish(dish));
        assertEquals("Dish description is required", ex.getMessage());
    }

    @Test
    void createDishEmptyImageUrl() {
        Dish dish = createDish(DISH_ID, "Pasta", 100, "desc", "", CATEGORY_ID);
        
        setupOwnerAuthentication();
        
        DomainException ex = assertThrows(DomainException.class, () -> useCase.createDish(dish));
        assertEquals("Dish imageUrl is required", ex.getMessage());
    }

    @Test
    void createDishEmptyRestaurantId() {
        Dish dish = new Dish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID, null, true);
        
        setupOwnerAuthentication();
        
        DomainException ex = assertThrows(DomainException.class, () -> useCase.createDish(dish));
        assertEquals("Dish restaurantId is required", ex.getMessage());
    }

    @Test
    void createDishNotOwner() {
        Dish dish = createDish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID);
        
        setupOtherUserAuthentication();
        when(restaurantServicePort.getRestaurantById(dish.getRestaurantId())).thenReturn(mockRestaurant);

        DomainException ex = assertThrows(DomainException.class, () -> useCase.createDish(dish));
        assertEquals("Only the restaurant owner can create or update dishes", ex.getMessage());
    }

    @Test
    void updateDishOnlyPrice() {
        Dish dish = createDish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID);
        Dish updatedDish = createDish(DISH_ID, "Pasta", 200, "desc", "img", CATEGORY_ID);
        
        setupOwnerAuthentication();
        when(dishPersistencePort.updateDish(dish)).thenReturn(Optional.of(updatedDish));
        when(dishPersistencePort.getById(DISH_ID)).thenReturn(Optional.of(dish));
        when(restaurantServicePort.getRestaurantById(dish.getRestaurantId())).thenReturn(mockRestaurant);

        Dish result = useCase.updateDish(DISH_ID, Optional.of(200), Optional.empty());
        
        assertEquals(200, result.getPrice());
        verify(dishPersistencePort).updateDish(dish);
    }

    @Test
    void updateDishOnlyDescription() {
        Dish dish = createDish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID);
        Dish updatedDish = createDish(DISH_ID, "Pasta", 100, "Nueva descripcion", "img", CATEGORY_ID);
        
        setupOwnerAuthentication();
        when(dishPersistencePort.updateDish(dish)).thenReturn(Optional.of(updatedDish));
        when(dishPersistencePort.getById(DISH_ID)).thenReturn(Optional.of(dish));
        when(restaurantServicePort.getRestaurantById(dish.getRestaurantId())).thenReturn(mockRestaurant);
        
        Dish result = useCase.updateDish(DISH_ID, Optional.empty(), Optional.of("Nueva descripcion"));
        
        assertEquals("Nueva descripcion", result.getDescription());
        verify(dishPersistencePort).updateDish(dish);
    }

    @Test
    void updateInvalidDishPriceAndDescription() {
        Dish dish = createDish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID);
        Optional<Integer> newPrice = Optional.empty();
        Optional<String> newDescription = Optional.empty();

        setupOwnerAuthentication();
        when(restaurantServicePort.getRestaurantById(dish.getRestaurantId())).thenReturn(mockRestaurant);
        when(dishPersistencePort.getById(DISH_ID)).thenReturn(Optional.of(dish));
        
        DomainException ex = assertThrows(DomainException.class, () -> useCase.updateDish(DISH_ID, newPrice, newDescription));
        assertEquals("At least one field (price or description) must be provided", ex.getMessage());
    }

    @Test
    void updateDishPriceAndDescription() {
        Dish dish = createDish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID);
        Dish updatedDish = createDish(DISH_ID, "Pasta", 300, "Otra descripcion", "img", CATEGORY_ID);
         
        setupOwnerAuthentication();
        when(dishPersistencePort.updateDish(dish)).thenReturn(Optional.of(updatedDish));
        when(dishPersistencePort.getById(DISH_ID)).thenReturn(Optional.of(dish));
        when(restaurantServicePort.getRestaurantById(dish.getRestaurantId())).thenReturn(mockRestaurant);
        
        Dish result = useCase.updateDish(DISH_ID, Optional.of(300), Optional.of("Otra descripcion"));
        
        assertEquals(300, result.getPrice());
        assertEquals("Otra descripcion", result.getDescription());
        verify(dishPersistencePort).updateDish(dish);
    }

    @Test
    void updateDishWithEmptyDescription() {
        Dish dish = createDish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID);
        Optional<Integer> newPrice = Optional.of(200);
        Optional<String> newDescription = Optional.of("");

        setupOwnerAuthentication();
        when(dishPersistencePort.getById(DISH_ID)).thenReturn(Optional.of(dish));
        when(restaurantServicePort.getRestaurantById(dish.getRestaurantId())).thenReturn(mockRestaurant);

        DomainException ex = assertThrows(DomainException.class, () -> useCase.updateDish(DISH_ID, newPrice, newDescription));
        assertEquals("Dish description cannot be empty", ex.getMessage());
    }

    @Test
    void updateDishWithNegativePrice() {
        Dish dish = createDish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID);
        Optional<Integer> newPrice = Optional.of(-2);
        Optional<String> newDescription = Optional.empty();

        setupOwnerAuthentication();
        when(dishPersistencePort.getById(DISH_ID)).thenReturn(Optional.of(dish));
        when(restaurantServicePort.getRestaurantById(dish.getRestaurantId())).thenReturn(mockRestaurant);

        DomainException ex = assertThrows(DomainException.class, () -> useCase.updateDish(DISH_ID, newPrice, newDescription));
        assertEquals("Dish price must be a positive integer", ex.getMessage());
    }

    @Test
    void createDishWithDuplicateName() {
        Dish dish = createDish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID);

        setupOwnerAuthentication();
        when(restaurantServicePort.getRestaurantById(dish.getRestaurantId())).thenReturn(mockRestaurant);
        when(dishPersistencePort.existsByNameAndRestaurantId("Pasta", RESTAURANT_ID)).thenReturn(true);
        
        DomainException ex = assertThrows(DomainException.class, () -> useCase.createDish(dish));
        assertEquals("A dish with this name already exists in this restaurant", ex.getMessage());
    }

    @Test
    void updateDishActiveToTrue() {
        Dish dish = new Dish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID, RESTAURANT_ID, false);
        Dish updatedDish = createDish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID);
        
        setupOwnerAuthentication();
        when(dishPersistencePort.getById(DISH_ID)).thenReturn(Optional.of(dish));
        when(dishPersistencePort.updateDishActive(dish)).thenReturn(Optional.of(updatedDish));
        when(restaurantServicePort.getRestaurantById(dish.getRestaurantId())).thenReturn(mockRestaurant);
        
        Dish result = useCase.updateDishActive(DISH_ID, Optional.of(true));
        assertTrue(result.isActive());
        verify(dishPersistencePort).updateDishActive(dish);
    }

    @Test
    void updateDishActiveToFalse() {
        Dish dish = createDish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID);
        Dish updatedDish = new Dish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID, RESTAURANT_ID, false);
        
        setupOwnerAuthentication();
        when(dishPersistencePort.getById(DISH_ID)).thenReturn(Optional.of(dish));
        when(dishPersistencePort.updateDishActive(dish)).thenReturn(Optional.of(updatedDish));
        when(restaurantServicePort.getRestaurantById(dish.getRestaurantId())).thenReturn(mockRestaurant);
        Dish result = useCase.updateDishActive(DISH_ID, Optional.of(false));
        
        assertFalse(result.isActive());
        verify(dishPersistencePort).updateDishActive(dish);
    }

    @Test
    void updateDishActiveThrowsIfNotOwner() {
        Dish dish = createDish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID);
        Optional<Boolean> newActive = Optional.of(false);

        setupOtherUserAuthentication();
        when(dishPersistencePort.getById(DISH_ID)).thenReturn(Optional.of(dish));
        when(restaurantServicePort.getRestaurantById(dish.getRestaurantId())).thenReturn(mockRestaurant);
        
        DomainException ex = assertThrows(DomainException.class, () -> useCase.updateDishActive(DISH_ID, newActive));
        assertEquals("Only the restaurant owner can create or update dishes", ex.getMessage());
    }

    @Test
    void updateDishActiveThrowsExceptionIfDishNotFound() {
        Optional<Boolean> newActive = Optional.of(false);

        setupOwnerAuthentication();
        when(dishPersistencePort.getById(DISH_ID)).thenReturn(Optional.empty());
        
        DomainException ex = assertThrows(DomainException.class, () -> useCase.updateDishActive(DISH_ID, newActive));
        assertEquals("Dish not found", ex.getMessage());
    }

    @Test
    void updateDishThrowsExceptionIfDishNotFound() {
        Optional<Integer> newPrice = Optional.of(200);
        Optional<String> newDescription = Optional.of("Nueva descripcion");
        
        setupOwnerAuthentication();
        when(dishPersistencePort.getById(DISH_ID)).thenReturn(Optional.empty());
        
        DomainException ex = assertThrows(DomainException.class, () -> useCase.updateDish(DISH_ID, newPrice, newDescription));
        assertEquals("Dish not found", ex.getMessage());
    }

    @Test
    void updateDishActiveThrowsIfActiveNotProvided() {
        Dish dish = createDish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID);
        Optional<Boolean> newActive = Optional.empty();

        setupOwnerAuthentication();
        when(dishPersistencePort.getById(DISH_ID)).thenReturn(Optional.of(dish));
        when(restaurantServicePort.getRestaurantById(dish.getRestaurantId())).thenReturn(mockRestaurant);
        
        DomainException ex = assertThrows(DomainException.class, () -> useCase.updateDishActive(DISH_ID, newActive));
        assertEquals("Active field must be provided", ex.getMessage());
    }

    @Test
    void getDishesByRestaurantWithCategoryIdShouldReturnFilteredDishes() {
        Dish dish2SameCategory = createDish(UUID.randomUUID(), DISH_NAME_2, DISH_PRICE_2, DISH_DESCRIPTION_2, DISH_IMAGE_URL_2, CATEGORY_ID);
        List<Dish> dishList = Arrays.asList(defaultDish, dish2SameCategory);
        Page<Dish> dishPage = new PageImpl<>(dishList, defaultPageRequest, 2);
        
        when(restaurantServicePort.existsById(RESTAURANT_ID)).thenReturn(true);
        when(categoryServicePort.existsById(CATEGORY_ID)).thenReturn(true);
        when(dishPersistencePort.getDishesByRestaurant(RESTAURANT_ID, Optional.of(CATEGORY_ID), defaultPageRequest))
            .thenReturn(dishPage);
        Page<Dish> result = useCase.getDishesByRestaurant(RESTAURANT_ID, Optional.of(CATEGORY_ID), defaultPageRequest);
        
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals(DISH_NAME, result.getContent().get(0).getName());
        assertEquals(DISH_NAME_2, result.getContent().get(1).getName());
        assertTrue(result.getContent().stream().allMatch(dish -> dish.getCategoryId().equals(CATEGORY_ID)));
        verify(restaurantServicePort).existsById(RESTAURANT_ID);
        verify(categoryServicePort).existsById(CATEGORY_ID);
        verify(dishPersistencePort).getDishesByRestaurant(RESTAURANT_ID, Optional.of(CATEGORY_ID), defaultPageRequest);
    }

    @Test
    void getDishesByRestaurantWithoutCategoryIdShouldReturnAllDishes() {
        List<Dish> dishList = Arrays.asList(defaultDish, dish2, dish3);
        Page<Dish> dishPage = new PageImpl<>(dishList, defaultPageRequest, 3);
        
        when(restaurantServicePort.existsById(RESTAURANT_ID)).thenReturn(true);
        when(dishPersistencePort.getDishesByRestaurant(RESTAURANT_ID, Optional.empty(), defaultPageRequest))
            .thenReturn(dishPage);
        Page<Dish> result = useCase.getDishesByRestaurant(RESTAURANT_ID, Optional.empty(), defaultPageRequest);
        
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(3, result.getContent().size());
        assertTrue(result.getContent().stream().allMatch(dish -> dish.getRestaurantId().equals(RESTAURANT_ID)));
        assertTrue(result.getContent().stream().allMatch(Dish::isActive));
        verify(restaurantServicePort).existsById(RESTAURANT_ID);
        verify(dishPersistencePort).getDishesByRestaurant(RESTAURANT_ID, Optional.empty(), defaultPageRequest);
    }

    @Test
    void getDishesByRestaurantWithPaginationShouldReturnCorrectPage() {
        PageRequest pageRequest = PageRequest.of(1, 2, Sort.by("name").ascending());
        List<Dish> dishList = Arrays.asList(dish3, dish2);
        Page<Dish> dishPage = new PageImpl<>(dishList, pageRequest, 6);
        
        when(restaurantServicePort.existsById(RESTAURANT_ID)).thenReturn(true);
        when(dishPersistencePort.getDishesByRestaurant(RESTAURANT_ID, Optional.empty(), pageRequest))
            .thenReturn(dishPage);
        Page<Dish> result = useCase.getDishesByRestaurant(RESTAURANT_ID, Optional.empty(), pageRequest);
        
        assertNotNull(result);
        assertEquals(6, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
        assertEquals(2, result.getContent().size());
        assertEquals(1, result.getNumber());
        assertFalse(result.isFirst());
        assertFalse(result.isLast());
        verify(restaurantServicePort).existsById(RESTAURANT_ID);
        verify(dishPersistencePort).getDishesByRestaurant(RESTAURANT_ID, Optional.empty(), pageRequest);
    }

    @Test
    void getDishesByRestaurantWithNonExistentCategoryShouldThrowException() {
        Integer nonExistentCategoryId = 999;
        Optional<Integer> newCategoryId = Optional.of(nonExistentCategoryId);
        
        when(restaurantServicePort.existsById(RESTAURANT_ID)).thenReturn(true);
        when(categoryServicePort.existsById(nonExistentCategoryId)).thenReturn(false);
        
        DomainException ex = assertThrows(DomainException.class, () -> 
            useCase.getDishesByRestaurant(RESTAURANT_ID, newCategoryId, defaultPageRequest));
        
        assertEquals("Category not found", ex.getMessage());
        verify(restaurantServicePort).existsById(RESTAURANT_ID);
        verify(categoryServicePort).existsById(nonExistentCategoryId);
    }

    @Test
    void getDishesByRestaurantWithNonExistentRestaurantShouldThrowException() {
        UUID nonExistentRestaurantId = UUID.randomUUID();
        Optional<Integer> newCategoryId = Optional.empty();

        when(restaurantServicePort.existsById(nonExistentRestaurantId)).thenReturn(false);

        DomainException ex = assertThrows(DomainException.class, () -> 
            useCase.getDishesByRestaurant(nonExistentRestaurantId, newCategoryId, defaultPageRequest));
        assertEquals("Restaurant not found", ex.getMessage());
        verify(restaurantServicePort).existsById(nonExistentRestaurantId);
    }

    @Test
    void updateDescriptionAndPriceThrowsExceptionIfCannotUpdateDish() {
        Dish dish = createDish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID);
        Optional<Integer> newPrice = Optional.of(200);
        Optional<String> newDescription = Optional.of("Nueva descripcion");
        
        setupOwnerAuthentication();
        when(dishPersistencePort.getById(DISH_ID)).thenReturn(Optional.of(dish));
        when(dishPersistencePort.updateDish(dish)).thenReturn(Optional.empty());
        when(restaurantServicePort.getRestaurantById(dish.getRestaurantId())).thenReturn(mockRestaurant);
        
        DomainException exception = assertThrows(DomainException.class, () -> 
            useCase.updateDish(DISH_ID, newPrice, newDescription)
        );
        assertEquals("Failed to update dish - dish not found after update", exception.getMessage());
        verify(dishPersistencePort).updateDish(dish);
    }

    @Test
    void updateDishActiveThrowsExceptionIfCannotUpdateDish() {
        Dish dish = createDish(DISH_ID, "Pasta", 100, "desc", "img", CATEGORY_ID);
        Optional<Boolean> newActive = Optional.of(true);
        
        setupOwnerAuthentication();
        when(dishPersistencePort.getById(DISH_ID)).thenReturn(Optional.of(dish));
        when(dishPersistencePort.updateDishActive(dish)).thenReturn(Optional.empty());
        when(restaurantServicePort.getRestaurantById(dish.getRestaurantId())).thenReturn(mockRestaurant);

        DomainException exception = assertThrows(DomainException.class, () -> 
            useCase.updateDishActive(DISH_ID, newActive)
        );
        assertEquals("Failed to update dish - dish not found after update", exception.getMessage());
        verify(dishPersistencePort).updateDishActive(dish);
    }
} 