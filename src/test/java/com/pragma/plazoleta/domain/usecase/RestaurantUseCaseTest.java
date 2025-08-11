package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.exception.DomainException;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.model.DomainPage;
import com.pragma.plazoleta.domain.model.EmployeeAverageTime;
import com.pragma.plazoleta.domain.model.OrderSummary;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazoleta.domain.spi.ISecurityContextPort;
import com.pragma.plazoleta.domain.spi.IUserRoleValidationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import com.pragma.plazoleta.domain.spi.ITraceCommunicationPort;
import org.mockito.Mockito;

import java.time.Duration;
import java.time.LocalDateTime;
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
    private ITraceCommunicationPort traceCommunicationPort;
    private RestaurantUseCase useCase;

    @BeforeEach
    void setUp() {
        restaurantPersistencePort = Mockito.mock(IRestaurantPersistencePort.class);
        userRoleValidationPort = Mockito.mock(IUserRoleValidationPort.class);
        securityContextPort = Mockito.mock(ISecurityContextPort.class);
        traceCommunicationPort = Mockito.mock(ITraceCommunicationPort.class);
        useCase = new RestaurantUseCase(restaurantPersistencePort, userRoleValidationPort, securityContextPort, traceCommunicationPort);
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
        DomainPage<Restaurant> emptyPage = DomainPage.<Restaurant>builder()
            .content(Collections.emptyList())
            .pageNumber(0)
            .pageSize(10)
            .totalElements(0)
            .build();
        
        when(restaurantPersistencePort.findAll(0, 10)).thenReturn(emptyPage);
        DomainPage<Restaurant> result = useCase.getAllRestaurants(0, 10);
        
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getContent().size());
        verify(restaurantPersistencePort).findAll(0, 10);
    }

    @Test
    void getAllRestaurantsWithPagination() {
        List<Restaurant> restaurantList = Arrays.asList(
            new Restaurant(UUID.randomUUID(), "Restaurante A", 111111111L, "Dirección A", "1111111111", "logo6.jpg", OWNER_ID)
        );
        DomainPage<Restaurant> restaurantPage = DomainPage.<Restaurant>builder()
            .content(restaurantList)
            .pageNumber(2)
            .pageSize(1)
            .totalElements(4)
            .build();
        
        when(restaurantPersistencePort.findAll(2, 1)).thenReturn(restaurantPage);
        DomainPage<Restaurant> result = useCase.getAllRestaurants(2, 1);
        
        assertNotNull(result);
        assertEquals(4, result.getTotalElements());
        assertEquals(4, result.getTotalPages());
        assertEquals(1, result.getContent().size());
        assertEquals(2, result.getPageNumber());
        verify(restaurantPersistencePort).findAll(2, 1);
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

    @Test
    void getEmployeeAverageTimeSuccessfully() {
        UUID restaurantId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        Restaurant restaurant = new Restaurant(restaurantId, "Test Restaurant", 1234L, "Address", "+573000000000", "logo", ownerId);
        List<EmployeeAverageTime> expectedResult = Arrays.asList(
            EmployeeAverageTime.builder()
                .employeeId(UUID.randomUUID())
                .averageTime(String.valueOf(Duration.ofMinutes(30)))
                .build()
        );
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("OWNER");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(ownerId);
        when(restaurantPersistencePort.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(traceCommunicationPort.getEmployeeAverageTime(restaurantId)).thenReturn(expectedResult);
        List<EmployeeAverageTime> result = useCase.getRestaurantEmployeesRanking(restaurantId);
        
        assertEquals(expectedResult, result);
        verify(securityContextPort).getRoleOfUserAutenticated();
        verify(securityContextPort).getUserIdOfUserAutenticated();
        verify(restaurantPersistencePort).findById(restaurantId);
        verify(traceCommunicationPort).getEmployeeAverageTime(restaurantId);
    }

    @Test
    void getEmployeeAverageTimeThrowsExceptionWhenNotOwnerOfRestaurant() {
        UUID restaurantId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID differentOwnerId = UUID.randomUUID();
        Restaurant restaurant = new Restaurant(restaurantId, "Test Restaurant", 1234L, "Address", "+573000000000", "logo", ownerId);
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("OWNER");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(differentOwnerId);
        when(restaurantPersistencePort.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        
        DomainException exception = assertThrows(DomainException.class, 
            () -> useCase.getRestaurantEmployeesRanking(restaurantId));
        assertEquals("You are not the owner of this restaurant", exception.getMessage());
        verify(securityContextPort).getRoleOfUserAutenticated();
        verify(securityContextPort).getUserIdOfUserAutenticated();
        verify(restaurantPersistencePort).findById(restaurantId);
        verifyNoInteractions(traceCommunicationPort);
    }

    @Test
    void getEmployeeAverageTimeThrowsExceptionWhenNotHasOwnerRole() {
        UUID restaurantId = UUID.randomUUID();
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("EMPLOYEE");
        
        DomainException exception = assertThrows(DomainException.class, 
            () -> useCase.getRestaurantEmployeesRanking(restaurantId));
        assertEquals("You are not the owner of this restaurant", exception.getMessage());
        verify(securityContextPort).getRoleOfUserAutenticated();
        verifyNoInteractions(restaurantPersistencePort, traceCommunicationPort);
    }

    @Test
    void getOrderSummaryThrowsExceptionWhenNotOwnerOfRestaurant() {
        UUID restaurantId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID differentOwnerId = UUID.randomUUID();
        Restaurant restaurant = new Restaurant(restaurantId, "Test Restaurant", 1234L, "Address", "+573000000000", "logo", ownerId);
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("OWNER");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(differentOwnerId);
        when(restaurantPersistencePort.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        DomainException exception = assertThrows(DomainException.class, 
            () -> useCase.getRestaurantOrdersSummary(restaurantId));
        assertEquals("You are not the owner of this restaurant", exception.getMessage());
        verify(securityContextPort).getRoleOfUserAutenticated();
        verify(securityContextPort).getUserIdOfUserAutenticated();
        verify(restaurantPersistencePort).findById(restaurantId);
        verifyNoInteractions(traceCommunicationPort);
    }

    @Test
    void getOrderSummaryThrowsExceptionWhenNotHasOwnerRole() {
        UUID restaurantId = UUID.randomUUID();
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("EMPLOYEE");
        
        DomainException exception = assertThrows(DomainException.class, 
            () -> useCase.getRestaurantOrdersSummary(restaurantId));
        assertEquals("You are not the owner of this restaurant", exception.getMessage());
        verify(securityContextPort).getRoleOfUserAutenticated();
        verifyNoInteractions(restaurantPersistencePort, traceCommunicationPort);
    }

    @Test
    void getOrderSummarySuccessfully() {
        UUID restaurantId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        Restaurant restaurant = new Restaurant(restaurantId, "Test Restaurant", 1234L, "Address", "+573000000000", "logo", ownerId);
        List<OrderSummary> expectedResult = Arrays.asList(
            OrderSummary.builder()
                .orderId(UUID.randomUUID())
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .finalStatus("DELIVERED")
                .employeeId(UUID.randomUUID().toString())
                .build()
        );
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("OWNER");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(ownerId);
        when(restaurantPersistencePort.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(traceCommunicationPort.getTraceByRestaurantId(restaurantId)).thenReturn(expectedResult);
        
        List<OrderSummary> result = useCase.getRestaurantOrdersSummary(restaurantId);
        assertEquals(expectedResult, result);
        verify(securityContextPort).getRoleOfUserAutenticated();
        verify(securityContextPort).getUserIdOfUserAutenticated();
        verify(restaurantPersistencePort).findById(restaurantId);
        verify(traceCommunicationPort).getTraceByRestaurantId(restaurantId);
    }
} 