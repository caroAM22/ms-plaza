package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.exception.DomainException;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazoleta.domain.spi.ISecurityContextPort;
import com.pragma.plazoleta.domain.spi.IUserRoleValidationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestaurantUseCaseTest {
    private static final String RESTAURANT_NAME = "Qbano";
    private static final String RESTAURANT_ADDRESS = "address";
    private static final UUID RESTAURANT_ID = UUID.randomUUID();
    private static final UUID OWNER_ID = UUID.randomUUID();
    
    private IRestaurantPersistencePort restaurantPersistencePort;
    private IUserRoleValidationPort userRoleValidationPort;
    private ISecurityContextPort securityContextPort;
    private RestaurantUseCase useCase;

    @BeforeEach
    void setUp() {
        restaurantPersistencePort = Mockito.mock(IRestaurantPersistencePort.class);
        userRoleValidationPort = Mockito.mock(IUserRoleValidationPort.class);
        securityContextPort = Mockito.mock(ISecurityContextPort.class);
        useCase = new RestaurantUseCase(restaurantPersistencePort, userRoleValidationPort, securityContextPort);
    }

    @Test
    void createRestaurantSuccess() {
        Restaurant restaurant = new Restaurant(RESTAURANT_ID, RESTAURANT_NAME, 1234L, RESTAURANT_ADDRESS, "+573000000000", "logo", OWNER_ID);
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("ADMIN");
        when(userRoleValidationPort.getRoleNameByUserId(OWNER_ID)).thenReturn(Optional.of("OWNER"));
        when(restaurantPersistencePort.existsByNit(anyLong())).thenReturn(false);
        when(restaurantPersistencePort.existsByName(anyString())).thenReturn(false);
        when(restaurantPersistencePort.save(any(Restaurant.class))).thenReturn(restaurant);
        
        Restaurant result = useCase.createRestaurant(restaurant);
        
        assertEquals(RESTAURANT_NAME, result.getName());
    }

    @ParameterizedTest
    @CsvSource({
        "'', 'Name is required'",
        "'123456789', 'Name must contain at least one letter'"
    })
    void createRestaurantThrowsWhenNameIsInvalid(String invalidName, String expectedMessage) {
        Restaurant restaurant = new Restaurant(RESTAURANT_ID, invalidName, 1234L, RESTAURANT_ADDRESS, "+573000000000", "logo", OWNER_ID);
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("ADMIN");
        Exception ex = assertThrows(DomainException.class, () -> useCase.createRestaurant(restaurant));
        
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void createRestaurantThrowsWhenNitExists() {
        Restaurant restaurant = new Restaurant(RESTAURANT_ID, RESTAURANT_NAME, 1234L, RESTAURANT_ADDRESS, "+573000000000", "logo", OWNER_ID);
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("ADMIN");
        when(userRoleValidationPort.getRoleNameByUserId(OWNER_ID)).thenReturn(Optional.of("OWNER"));
        when(restaurantPersistencePort.existsByNit(anyLong())).thenReturn(true);
        Exception ex = assertThrows(DomainException.class, () -> useCase.createRestaurant(restaurant));
        
        assertEquals("NIT already exists", ex.getMessage());
    }

    @Test
    void createRestaurantThrowsWhenUserRoleIsInvalid() {
        Restaurant restaurant = new Restaurant(RESTAURANT_ID, RESTAURANT_NAME, 1234L, RESTAURANT_ADDRESS, "+573000000000", "logo", OWNER_ID);
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("ADMIN");
        when(userRoleValidationPort.getRoleNameByUserId(OWNER_ID)).thenReturn(Optional.empty());
        Exception ex = assertThrows(DomainException.class, () -> useCase.createRestaurant(restaurant));
        
        assertEquals("User not found or has no role", ex.getMessage());
    }

    @Test
    void createRestaurantThrowsWhenNameExists() {
        Restaurant restaurant = new Restaurant(RESTAURANT_ID, RESTAURANT_NAME, 1234L, RESTAURANT_ADDRESS, "+573000000000", "logo", OWNER_ID);
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("ADMIN");
        when(userRoleValidationPort.getRoleNameByUserId(OWNER_ID)).thenReturn(Optional.of("OWNER"));
        when(restaurantPersistencePort.existsByNit(anyLong())).thenReturn(false);
        when(restaurantPersistencePort.existsByName(anyString())).thenReturn(true);
        
        Exception ex = assertThrows(DomainException.class, () -> useCase.createRestaurant(restaurant));
        
        assertEquals("A restaurant with this name already exists", ex.getMessage());
    }

    @Test
    void createRestaurantThrowsWhenOwnerRoleInvalid() {
        Restaurant restaurant = new Restaurant(RESTAURANT_ID, RESTAURANT_NAME, 1234L, RESTAURANT_ADDRESS, "+573000000000", "logo", OWNER_ID);
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("ADMIN");
        when(userRoleValidationPort.getRoleNameByUserId(OWNER_ID)).thenReturn(Optional.of("CUSTOMER"));
        when(restaurantPersistencePort.existsByNit(anyLong())).thenReturn(false);
        when(restaurantPersistencePort.existsByName(anyString())).thenReturn(false);
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
        Restaurant restaurant = new Restaurant(RESTAURANT_ID, RESTAURANT_NAME, 1234L, RESTAURANT_ADDRESS, invalidPhone, "logo", OWNER_ID);
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("ADMIN");
        when(userRoleValidationPort.getRoleNameByUserId(OWNER_ID)).thenReturn(Optional.of("OWNER"));
        when(restaurantPersistencePort.existsByNit(anyLong())).thenReturn(false);
        when(restaurantPersistencePort.existsByName(anyString())).thenReturn(false);
        
        Exception ex = assertThrows(DomainException.class, () -> useCase.createRestaurant(restaurant));
        assertEquals("Phone must be numeric and may start with +", ex.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
        "'', 'Phone is required'",
        "'+57315879692699', 'Phone must not exceed 13 characters'",
        "'', 'Address is required'",
        "'', 'Logo URL is required'"
    })
    void createRestaurantThrowsWhenRequiredFieldsAreEmpty(String emptyValue, String expectedMessage) {
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("ADMIN");
        
        Restaurant restaurant;
        if (expectedMessage.equals("Phone is required")) {
            restaurant = new Restaurant(RESTAURANT_ID, RESTAURANT_NAME, 1234L, RESTAURANT_ADDRESS, emptyValue, "logo", OWNER_ID);
        } else if (expectedMessage.equals("Address is required")) {
            restaurant = new Restaurant(RESTAURANT_ID, RESTAURANT_NAME, 1234L, emptyValue, "+573158796926", "logo", OWNER_ID);
        } else if (expectedMessage.equals("Logo URL is required")) {
            restaurant = new Restaurant(RESTAURANT_ID, RESTAURANT_NAME, 1234L, RESTAURANT_ADDRESS, "+573158796926", emptyValue, OWNER_ID);
        } else {
            restaurant = new Restaurant(RESTAURANT_ID, RESTAURANT_NAME, 1234L, RESTAURANT_ADDRESS, emptyValue, "logo", OWNER_ID);
        }
        
        Exception ex = assertThrows(DomainException.class, () -> useCase.createRestaurant(restaurant));
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void createRestaurantThrowsWhenNitIsNegative() {
        Restaurant restaurant = new Restaurant(RESTAURANT_ID, RESTAURANT_NAME, -1234L, RESTAURANT_ADDRESS, "+573158796926", "logo", OWNER_ID);
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("ADMIN");
        
        Exception ex = assertThrows(DomainException.class, () -> useCase.createRestaurant(restaurant));
        assertEquals("NIT is required and must be positive", ex.getMessage());
    }

    @Test
    void createRestaurantThrowsWhenOwnerIdIsRequired() {
        Restaurant restaurant = new Restaurant(RESTAURANT_ID, RESTAURANT_NAME, 1234L, RESTAURANT_ADDRESS, "+573158796926", "logo", null);
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("ADMIN");
        
        Exception ex = assertThrows(DomainException.class, () -> useCase.createRestaurant(restaurant));
        assertEquals("Owner ID is required", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1",
            "1234567890123",
            "+123456789123",
            "123456"
    })
    void createRestaurantDoesNotThrowWhenValidPhone(String validPhone) {
        Restaurant restaurant = new Restaurant(RESTAURANT_ID, RESTAURANT_NAME, 1234L, RESTAURANT_ADDRESS, validPhone, "logo", OWNER_ID);
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("ADMIN");
        when(userRoleValidationPort.getRoleNameByUserId(OWNER_ID)).thenReturn(Optional.of("OWNER"));
        when(restaurantPersistencePort.existsByNit(anyLong())).thenReturn(false);
        when(restaurantPersistencePort.existsByName(anyString())).thenReturn(false);
        when(restaurantPersistencePort.save(any(Restaurant.class))).thenReturn(restaurant);
        
        assertDoesNotThrow(() -> useCase.createRestaurant(restaurant));
    }

    @Test
    void createRestaurantThrowsWhenRoleIsNotAdmin() {
        Restaurant restaurant = new Restaurant(RESTAURANT_ID, RESTAURANT_NAME, 1234L, RESTAURANT_ADDRESS, "+573000000000", "logo", OWNER_ID);
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("OWNER");
        
        DomainException ex = assertThrows(DomainException.class, () -> useCase.createRestaurant(restaurant));
        assertEquals("Only an ADMIN can create restaurants", ex.getMessage());
    }

    @Test
    void getAllRestaurantsEmptyPage() {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<Restaurant> emptyPage = new PageImpl<>(Collections.emptyList(), pageRequest, 0);
        
        when(restaurantPersistencePort.findAll(pageRequest)).thenReturn(emptyPage);
        Page<Restaurant> result = useCase.getAllRestaurants(pageRequest);
        

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getContent().size());
        assertTrue(result.isEmpty());
        verify(restaurantPersistencePort).findAll(pageRequest);
    }

    @Test
    void getAllRestaurantsWithPagination() {
        PageRequest pageRequest = PageRequest.of(2, 1, Sort.by("name").ascending());
        List<Restaurant> restaurantList = Arrays.asList(
            new Restaurant(UUID.randomUUID(), "Restaurante A", 111111111L, "Dirección A", "1111111111", "logo6.jpg", OWNER_ID)
        );
        Page<Restaurant> restaurantPage = new PageImpl<>(restaurantList, pageRequest, 4); 
        
        when(restaurantPersistencePort.findAll(pageRequest)).thenReturn(restaurantPage);
        Page<Restaurant> result = useCase.getAllRestaurants(pageRequest);
        
        assertNotNull(result);
        assertEquals(4, result.getTotalElements());
        assertEquals(4, result.getTotalPages());
        assertEquals(1, result.getContent().size());
        assertEquals(2, result.getNumber());
        assertFalse(result.isFirst());
        assertFalse(result.isLast());
        verify(restaurantPersistencePort).findAll(pageRequest);
    }

    @Test
    void getRestaurantByIdThrowsExceptionIfRestaurantNotFound() {
        when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.empty());
        
        DomainException ex = assertThrows(DomainException.class, () -> useCase.getRestaurantById(RESTAURANT_ID));
        assertEquals("Restaurant not found", ex.getMessage());
    }

    @Test
    void existsByIdReturnsTrueIfRestaurantFound() {
        when(restaurantPersistencePort.existsById(RESTAURANT_ID)).thenReturn(true);
        
        boolean result = useCase.existsById(RESTAURANT_ID);
        assertTrue(result);
    }

    @Test
    void getRestaurantByIdReturnsRestaurantIfFound() {
        Restaurant restaurant = new Restaurant(RESTAURANT_ID, RESTAURANT_NAME, 1234L, RESTAURANT_ADDRESS, "+573158796926", "logo", OWNER_ID);
        when(restaurantPersistencePort.findById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
        
        Restaurant result = useCase.getRestaurantById(RESTAURANT_ID);
        assertEquals(restaurant, result);
    }

    @Test
    void existsByIdReturnsFalseIfRestaurantNotFound() {
        when(restaurantPersistencePort.existsById(RESTAURANT_ID)).thenReturn(false);
        
        boolean result = useCase.existsById(RESTAURANT_ID);
        assertFalse(result);
    }
} 