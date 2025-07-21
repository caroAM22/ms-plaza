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
        Restaurant restaurant = new Restaurant("id", "Qbano", 1234L, "address", "+573000000000", "logo", "owner");
        
        when(userRoleValidationPort.hasOwnerRole(anyString())).thenReturn(true);
        when(persistencePort.existsByNit(anyLong())).thenReturn(false);
        when(persistencePort.existsByName(anyString())).thenReturn(false);
        when(persistencePort.save(any(Restaurant.class))).thenReturn(restaurant);
        Restaurant result = useCase.createRestaurant(restaurant);
        
        assertEquals("Qbano", result.getName());
    }

    @Test
    void createRestaurantThrowsWhenNameIsBlank() {
        Restaurant restaurant = new Restaurant("id", "", 1234L, "address", "+573000000000", "logo", "owner");
        
        Exception ex = assertThrows(DomainException.class, () -> useCase.createRestaurant(restaurant));
        
        assertEquals("Name is required", ex.getMessage());
    }

    @Test
    void createRestaurantThrowsWhenNameIsOnlyNumbers() {
        Restaurant restaurant = new Restaurant("id", "123456789", 1234L, "address", "+573000000000", "logo", "owner");
        
        Exception ex = assertThrows(DomainException.class, () -> useCase.createRestaurant(restaurant));
        
        assertEquals("Name must contain at least one letter", ex.getMessage());
    }

    @Test
    void createRestaurantThrowsWhenNitExists() {
        Restaurant restaurant = new Restaurant("id", "Qbano", 1234L, "address", "+573000000000", "logo", "owner");
        
        when(userRoleValidationPort.hasOwnerRole(anyString())).thenReturn(true);
        when(persistencePort.existsByNit(anyLong())).thenReturn(true);
        Exception ex = assertThrows(DomainException.class, () -> useCase.createRestaurant(restaurant));
        
        assertEquals("NIT already exists", ex.getMessage());
    }

    @Test
    void createRestaurantThrowsWhenNameExists() {
        Restaurant restaurant = new Restaurant("id", "Qbano", 1234L, "address", "+573000000000", "logo", "owner");
        
        when(userRoleValidationPort.hasOwnerRole(anyString())).thenReturn(true);
        when(persistencePort.existsByNit(anyLong())).thenReturn(false);
        when(persistencePort.existsByName(anyString())).thenReturn(true);
        Exception ex = assertThrows(DomainException.class, () -> useCase.createRestaurant(restaurant));
        
        assertEquals("A restaurant with this name already exists", ex.getMessage());
    }

    @Test
    void createRestaurantThrowsWhenOwnerRoleInvalid() {
        Restaurant restaurant = new Restaurant("id", "Qbano", 1234L, "address", "+573000000000", "logo", "owner");
        
        when(userRoleValidationPort.hasOwnerRole(anyString())).thenReturn(false);
        when(persistencePort.existsByNit(anyLong())).thenReturn(false);
        when(persistencePort.existsByName(anyString())).thenReturn(false);
        Exception ex = assertThrows(DomainException.class, () -> useCase.createRestaurant(restaurant));
        
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
        Restaurant restaurant = new Restaurant("id", "Qbano", 1234L, "address", invalidPhone, "logo", "owner");
        when(userRoleValidationPort.hasOwnerRole(anyString())).thenReturn(true);
        when(persistencePort.existsByNit(anyLong())).thenReturn(false);
        when(persistencePort.existsByName(anyString())).thenReturn(false);
        Exception ex = assertThrows(DomainException.class, () -> useCase.createRestaurant(restaurant));
        assertEquals("Phone must be numeric and may start with +", ex.getMessage());
    }

    @Test
    void createRestaurantThrowsWhenPhoneIsEmpty() {
        Restaurant restaurant = new Restaurant("id", "Qbano", 1234L, "address", "", "logo", "owner");
        Exception ex = assertThrows(DomainException.class, () -> useCase.createRestaurant(restaurant));
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
        Restaurant restaurant = new Restaurant("id", "Qbano", 1234L, "address", validPhone, "logo", "owner");
        when(userRoleValidationPort.hasOwnerRole(anyString())).thenReturn(true);
        when(persistencePort.existsByNit(anyLong())).thenReturn(false);
        when(persistencePort.existsByName(anyString())).thenReturn(false);
        when(persistencePort.save(any(Restaurant.class))).thenReturn(restaurant);
        assertDoesNotThrow(() -> useCase.createRestaurant(restaurant));
    }
} 