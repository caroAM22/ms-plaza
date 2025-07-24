package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.exception.DomainException;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazoleta.domain.spi.IUserRoleValidationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestaurantUseCaseTest {
    private static final String RESTAURANT_NAME = "Qbano";
    private static final String RESTAURANT_ADDRESS = "address";
    private IRestaurantPersistencePort persistencePort;
    private IUserRoleValidationPort userRoleValidationPort;
    private RestaurantUseCase useCase;

    @BeforeEach
    void setUp() {
        persistencePort = mock(IRestaurantPersistencePort.class);
        userRoleValidationPort = mock(IUserRoleValidationPort.class);
        useCase = new RestaurantUseCase(persistencePort, userRoleValidationPort);
    }

    @Test
    void createRestaurant() {
        Restaurant restaurant = new Restaurant("id", RESTAURANT_NAME, 1234L, RESTAURANT_ADDRESS, "+573000000000", "logo", "owner");
        
        when(userRoleValidationPort.getRoleNameByUserId(anyString())).thenReturn("OWNER");
        when(persistencePort.existsByNit(anyLong())).thenReturn(false);
        when(persistencePort.existsByName(anyString())).thenReturn(false);
        when(persistencePort.save(any(Restaurant.class))).thenReturn(restaurant);
        Restaurant result = useCase.createRestaurant(restaurant, "ADMIN");
        
        assertEquals(RESTAURANT_NAME, result.getName());
    }

    @Test
    void createRestaurantThrowsWhenNameIsBlank() {
        Restaurant restaurant = new Restaurant("id", "", 1234L, RESTAURANT_ADDRESS, "+573000000000", "logo", "owner");
        
        Exception ex = assertThrows(DomainException.class, () -> useCase.createRestaurant(restaurant, "ADMIN"));
        
        assertEquals("Name is required", ex.getMessage());
    }

    @Test
    void createRestaurantThrowsWhenNameIsOnlyNumbers() {
        Restaurant restaurant = new Restaurant("id", "123456789", 1234L, RESTAURANT_ADDRESS, "+573000000000", "logo", "owner");
        
        Exception ex = assertThrows(DomainException.class, () -> useCase.createRestaurant(restaurant, "ADMIN"));
        
        assertEquals("Name must contain at least one letter", ex.getMessage());
    }

    @Test
    void createRestaurantThrowsWhenNitExists() {
        Restaurant restaurant = new Restaurant("id", RESTAURANT_NAME, 1234L, RESTAURANT_ADDRESS, "+573000000000", "logo", "owner");
        
        when(userRoleValidationPort.getRoleNameByUserId(anyString())).thenReturn("OWNER");
        when(persistencePort.existsByNit(anyLong())).thenReturn(true);
        Exception ex = assertThrows(DomainException.class, () -> useCase.createRestaurant(restaurant, "ADMIN"));
        
        assertEquals("NIT already exists", ex.getMessage());
    }

    @Test
    void createRestaurantThrowsWhenNameExists() {
        Restaurant restaurant = new Restaurant("id", RESTAURANT_NAME, 1234L, RESTAURANT_ADDRESS, "+573000000000", "logo", "owner");
        
        when(userRoleValidationPort.getRoleNameByUserId(anyString())).thenReturn("OWNER");
        when(persistencePort.existsByNit(anyLong())).thenReturn(false);
        when(persistencePort.existsByName(anyString())).thenReturn(true);
        Exception ex = assertThrows(DomainException.class, () -> useCase.createRestaurant(restaurant, "ADMIN"));
        
        assertEquals("A restaurant with this name already exists", ex.getMessage());
    }

    @Test
    void createRestaurantThrowsWhenOwnerRoleInvalid() {
        Restaurant restaurant = new Restaurant("id", RESTAURANT_NAME, 1234L, RESTAURANT_ADDRESS, "+573000000000", "logo", "owner");
        
        when(userRoleValidationPort.hasOwnerRole()).thenReturn(false);
        when(persistencePort.existsByNit(anyLong())).thenReturn(false);
        when(persistencePort.existsByName(anyString())).thenReturn(false);
        Exception ex = assertThrows(DomainException.class, () -> useCase.createRestaurant(restaurant, "ADMIN"));
        
        assertEquals("User must have OWNER role", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "123+456789",
            "abc123def",
            "123-456-7890",
            "123abc456",
            "phone123",
            "123@456"
    })
    void createRestaurantThrowsWhenPhoneHasInvalidFormat(String invalidPhone) {
        Restaurant restaurant = new Restaurant("id", RESTAURANT_NAME, 1234L, RESTAURANT_ADDRESS, invalidPhone, "logo", "owner");
        when(userRoleValidationPort.hasOwnerRole()).thenReturn(true);
        when(persistencePort.existsByNit(anyLong())).thenReturn(false);
        when(persistencePort.existsByName(anyString())).thenReturn(false);
        Exception ex = assertThrows(DomainException.class, () -> useCase.createRestaurant(restaurant, "ADMIN"));
        assertEquals("Phone must be numeric and may start with +", ex.getMessage());
    }

    @Test
    void createRestaurantThrowsWhenPhoneIsEmpty() {
        Restaurant restaurant = new Restaurant("id", RESTAURANT_NAME, 1234L, RESTAURANT_ADDRESS, "", "logo", "owner");
        Exception ex = assertThrows(DomainException.class, () -> useCase.createRestaurant(restaurant, "ADMIN"));
        assertEquals("Phone is required", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1",
            "1234567890123",
            "+123456789123",
            "123456"
    })
    void createRestaurantDoesNotThrowWhenValidPhone(String validPhone) {
        Restaurant restaurant = new Restaurant("id", RESTAURANT_NAME, 1234L, RESTAURANT_ADDRESS, validPhone, "logo", "owner");
        when(userRoleValidationPort.getRoleNameByUserId(anyString())).thenReturn("OWNER");
        when(persistencePort.existsByNit(anyLong())).thenReturn(false);
        when(persistencePort.existsByName(anyString())).thenReturn(false);
        when(persistencePort.save(any(Restaurant.class))).thenReturn(restaurant);
        assertDoesNotThrow(() -> useCase.createRestaurant(restaurant, "ADMIN"));
    }

    @Test
    void createRestaurantThrowsWhenPhoneExceeds13Characters() {
        Restaurant restaurant = new Restaurant("id", RESTAURANT_NAME, 1234L, RESTAURANT_ADDRESS, "+57315879692699", "logo", "owner");
        Exception ex = assertThrows(DomainException.class, () -> useCase.createRestaurant(restaurant, "ADMIN"));
        assertEquals("Phone must not exceed 13 characters", ex.getMessage());
    }

    @Test
    void createRestaurantThrowsWhenNitIsNegative() {
        Restaurant restaurant = new Restaurant("id", RESTAURANT_NAME, -1234L, RESTAURANT_ADDRESS, "+573158796926", "logo", "owner");
        Exception ex = assertThrows(DomainException.class, () -> useCase.createRestaurant(restaurant, "ADMIN"));
        assertEquals("NIT is required and must be positive", ex.getMessage());
    }

    @Test
    void createRestaurantThrowsWhenLogoIsRequired() {
        Restaurant restaurant = new Restaurant("id", RESTAURANT_NAME, 1234L, RESTAURANT_ADDRESS, "+573158796926", "", "owner");
        Exception ex = assertThrows(DomainException.class, () -> useCase.createRestaurant(restaurant, "ADMIN"));
        assertEquals("Logo URL is required", ex.getMessage());
    }

    @Test
    void createRestaurantThrowsWhenOwnerIdIsRequired() {
        Restaurant restaurant = new Restaurant("id", RESTAURANT_NAME, 1234L, RESTAURANT_ADDRESS, "+573158796926", "logo", "");
        Exception ex = assertThrows(DomainException.class, () -> useCase.createRestaurant(restaurant, "ADMIN"));
        assertEquals("Owner ID is required", ex.getMessage());
    }

    @Test
    void createRestaurantThrowsWhenAddressIsRequired() {
        Restaurant restaurant = new Restaurant("id", RESTAURANT_NAME, 1234L, "", "+573158796926", "logo", "owner");
        Exception ex = assertThrows(DomainException.class, () -> useCase.createRestaurant(restaurant, "ADMIN"));
        assertEquals("Address is required", ex.getMessage());
    }
} 