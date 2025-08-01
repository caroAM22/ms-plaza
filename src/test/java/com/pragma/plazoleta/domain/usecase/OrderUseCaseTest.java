package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.api.IDishServicePort;
import com.pragma.plazoleta.domain.api.IRestaurantServicePort;
import com.pragma.plazoleta.domain.exception.OrderException;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderDish;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.domain.spi.IOrderPersistencePort;
import com.pragma.plazoleta.domain.spi.ISecurityContextPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.pragma.plazoleta.domain.spi.IUserRoleValidationPort;
import java.util.Optional;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@ExtendWith(MockitoExtension.class)
class OrderUseCaseTest {

    @Mock
    private IOrderPersistencePort orderPersistencePort;

    @Mock
    private IDishServicePort dishServicePort;

    @Mock
    private IRestaurantServicePort restaurantServicePort;

    @Mock
    private ISecurityContextPort securityContextPort;

    @Mock
    private IUserRoleValidationPort userRoleValidationPort;

    @InjectMocks
    private OrderUseCase orderUseCase;

    private Order order;
    private UUID clientId;
    private UUID restaurantId;
    private UUID dishId1;
    private UUID dishId2;
    private UUID orderId;
    private UUID employeeId;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
        restaurantId = UUID.randomUUID();
        dishId1 = UUID.randomUUID();
        dishId2 = UUID.randomUUID();
        orderId = UUID.randomUUID();
        employeeId = UUID.randomUUID();

        List<OrderDish> orderDishes = Arrays.asList(
            OrderDish.builder()
                .dishId(dishId1)
                .quantity(2)
                .build(),
            OrderDish.builder()
                .dishId(dishId2)
                .quantity(1)
                .build()
        );

        order = Order.builder()
            .restaurantId(restaurantId)
            .orderDishes(orderDishes)
            .status(OrderStatus.PENDING)
            .date(LocalDateTime.now())
            .build();
    }

    private Order createTestOrder(UUID restaurantId, OrderStatus status) {
        Order orders = new Order();
        orders.setId(orderId);
        orders.setRestaurantId(restaurantId);
        orders.setStatus(status);
        orders.setClientId(UUID.randomUUID());
        orders.setDate(LocalDateTime.now());
        orders.setOrderDishes(Collections.emptyList());
        return orders;
    }

    @Test
    void createOrderSuccess() {
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("CUSTOMER");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(clientId);
        when(orderPersistencePort.hasActiveOrders(clientId)).thenReturn(false);
        when(restaurantServicePort.existsById(restaurantId)).thenReturn(true);
        when(dishServicePort.existsById(dishId1)).thenReturn(true);
        when(dishServicePort.existsById(dishId2)).thenReturn(true);
        when(dishServicePort.isActiveById(dishId1)).thenReturn(true);
        when(dishServicePort.isActiveById(dishId2)).thenReturn(true);
        when(orderPersistencePort.saveOrder(any(Order.class))).thenReturn(order);

        Order result = orderUseCase.createOrder(order);

        assertNotNull(result);
        assertEquals(clientId, order.getClientId());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertNotNull(order.getDate());
        verify(orderPersistencePort).saveOrder(order);
    }

    @Test
    void createOrderNotCustomerRoleThrowsException() {
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("ADMIN");

        OrderException exception = assertThrows(OrderException.class, () -> orderUseCase.createOrder(order));
        assertEquals("You are not a CUSTOMER", exception.getMessage());
    }

    @Test
    void createOrderHasActiveOrdersThrowsException() {
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("CUSTOMER");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(clientId);
        when(orderPersistencePort.hasActiveOrders(clientId)).thenReturn(true);

        OrderException exception = assertThrows(OrderException.class, () -> orderUseCase.createOrder(order));
        assertEquals("You cannot have more than one active order", exception.getMessage());
    }

    @Test
    void createOrderRestaurantDoesNotExistThrowsException() {
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("CUSTOMER");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(clientId);
        when(orderPersistencePort.hasActiveOrders(clientId)).thenReturn(false);
        when(restaurantServicePort.existsById(restaurantId)).thenReturn(false);

        OrderException exception = assertThrows(OrderException.class, () -> orderUseCase.createOrder(order));
        assertEquals("Restaurant does not exist", exception.getMessage());
    }

    @Test
    void createOrderDishDoesNotExistThrowsException() {
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("CUSTOMER");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(clientId);
        when(orderPersistencePort.hasActiveOrders(clientId)).thenReturn(false);
        when(restaurantServicePort.existsById(restaurantId)).thenReturn(true);
        when(dishServicePort.existsById(dishId1)).thenReturn(false);

        OrderException exception = assertThrows(OrderException.class, () -> orderUseCase.createOrder(order));
        assertEquals("Dish with id " + dishId1 + " does not exist", exception.getMessage());
    }

    @Test
    void hasActiveOrdersReturnsTrue() {
        when(orderPersistencePort.hasActiveOrders(clientId)).thenReturn(true);

        boolean result = orderUseCase.hasActiveOrders(clientId);

        assertTrue(result);
        verify(orderPersistencePort).hasActiveOrders(clientId);
    }

    @Test
    void hasActiveOrdersReturnsFalse() {
        when(orderPersistencePort.hasActiveOrders(clientId)).thenReturn(false);

        boolean result = orderUseCase.hasActiveOrders(clientId);

        assertFalse(result);
        verify(orderPersistencePort).hasActiveOrders(clientId);
    }

    @Test
    void createOrderEmptyDishesListThrowsException() {
        Order orderWithEmptyDishes = Order.builder()
            .restaurantId(restaurantId)
            .orderDishes(Collections.emptyList())
            .build();
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("CUSTOMER");
        
        OrderException exception = assertThrows(OrderException.class, () -> orderUseCase.createOrder(orderWithEmptyDishes));
        assertEquals("Order must contain at least one dish", exception.getMessage());
    }

    @Test
    void createOrderNullDishesListThrowsException() {
        Order orderWithNullDishes = Order.builder()
            .restaurantId(restaurantId)
            .orderDishes(null)
            .build();
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("CUSTOMER");

        OrderException exception = assertThrows(OrderException.class, () -> orderUseCase.createOrder(orderWithNullDishes));
        assertEquals("Order must contain at least one dish", exception.getMessage());
    }

    @Test
    void createOrderWithZeroQuantityThrowsException() {
        List<OrderDish> orderDishesWithZeroQuantity = Arrays.asList(
            OrderDish.builder()
                .dishId(dishId1)
                .quantity(0)
                .build()
        );

        Order orderWithZeroQuantity = Order.builder()
            .restaurantId(restaurantId)
            .orderDishes(orderDishesWithZeroQuantity)
            .build();

        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("CUSTOMER");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(clientId);
        when(orderPersistencePort.hasActiveOrders(clientId)).thenReturn(false);
        when(restaurantServicePort.existsById(restaurantId)).thenReturn(true);
        when(dishServicePort.existsById(dishId1)).thenReturn(true);
        when(dishServicePort.isActiveById(dishId1)).thenReturn(true);

        OrderException exception = assertThrows(OrderException.class, () -> orderUseCase.createOrder(orderWithZeroQuantity));
        assertEquals("Quantity must be greater than 0", exception.getMessage());
    }

    @Test
    void createOrderWithNegativeQuantityThrowsException() {
        List<OrderDish> orderDishesWithNegativeQuantity = Arrays.asList(
            OrderDish.builder()
                .dishId(dishId1)
                .quantity(-1)
                .build()
        );

        Order orderWithNegativeQuantity = Order.builder()
            .restaurantId(restaurantId)
            .orderDishes(orderDishesWithNegativeQuantity)
            .build();

        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("CUSTOMER");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(clientId);
        when(orderPersistencePort.hasActiveOrders(clientId)).thenReturn(false);
        when(restaurantServicePort.existsById(restaurantId)).thenReturn(true);
        when(dishServicePort.existsById(dishId1)).thenReturn(true);
        when(dishServicePort.isActiveById(dishId1)).thenReturn(true);

        OrderException exception = assertThrows(OrderException.class, () -> orderUseCase.createOrder(orderWithNegativeQuantity));
        assertEquals("Quantity must be greater than 0", exception.getMessage());
    }

    @Test
    void createOrderWithNullQuantityThrowsException() {
        List<OrderDish> orderDishesWithNullQuantity = Arrays.asList(
            OrderDish.builder()
                .dishId(dishId1)
                .quantity(null)
                .build()
        );

        Order orderWithNullQuantity = Order.builder()
            .restaurantId(restaurantId)
            .orderDishes(orderDishesWithNullQuantity)
            .build();

        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("CUSTOMER");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(clientId);
        when(orderPersistencePort.hasActiveOrders(clientId)).thenReturn(false);
        when(restaurantServicePort.existsById(restaurantId)).thenReturn(true);
        when(dishServicePort.existsById(dishId1)).thenReturn(true);
        when(dishServicePort.isActiveById(dishId1)).thenReturn(true);

        OrderException exception = assertThrows(OrderException.class, () -> orderUseCase.createOrder(orderWithNullQuantity));
        assertEquals("Quantity is required", exception.getMessage());
    }

    @Test
    void createOrderWithNullDishIdThrowsException() {
        List<OrderDish> orderDishesWithNullDishId = Arrays.asList(
            OrderDish.builder()
                .dishId(null)
                .quantity(1)
                .build()
        );

        Order orderWithNullDishId = Order.builder()
            .restaurantId(restaurantId)
            .orderDishes(orderDishesWithNullDishId)
            .build();

        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("CUSTOMER");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(clientId);
        when(orderPersistencePort.hasActiveOrders(clientId)).thenReturn(false);
        when(restaurantServicePort.existsById(restaurantId)).thenReturn(true);

        OrderException exception = assertThrows(OrderException.class, () -> orderUseCase.createOrder(orderWithNullDishId));
        assertEquals("Dish id is required", exception.getMessage());
    }

    @Test
    void createOrderWithDuplicateDishesThrowsException() {
        List<OrderDish> orderDishesWithDuplicates = Arrays.asList(
            OrderDish.builder()
                .dishId(dishId1)
                .quantity(1)
                .build(),
            OrderDish.builder()
                .dishId(dishId1) 
                .quantity(2)
                .build()
        );

        Order orderWithDuplicates = Order.builder()
            .restaurantId(restaurantId)
            .orderDishes(orderDishesWithDuplicates)
            .build();
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("CUSTOMER");

        OrderException exception = assertThrows(OrderException.class, () -> orderUseCase.createOrder(orderWithDuplicates));
        assertEquals("Duplicate dish with id " + dishId1 + " found in order", exception.getMessage());
    }

    @Test
    void createOrderWithInactiveDishThrowsException() {
        List<OrderDish> orderDishesWithInactiveDish = Arrays.asList(
            OrderDish.builder()
                .dishId(dishId1)
                .quantity(1)
                .build()
        );

        Order orderWithInactiveDish = Order.builder()
            .restaurantId(restaurantId)
            .orderDishes(orderDishesWithInactiveDish)
            .build();

        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("CUSTOMER");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(clientId);
        when(orderPersistencePort.hasActiveOrders(clientId)).thenReturn(false);
        when(restaurantServicePort.existsById(restaurantId)).thenReturn(true);
        when(dishServicePort.existsById(dishId1)).thenReturn(true);
        when(dishServicePort.isActiveById(dishId1)).thenReturn(false);

        OrderException exception = assertThrows(OrderException.class, () -> orderUseCase.createOrder(orderWithInactiveDish));
        assertEquals("Dish with id " + dishId1 + " is not active", exception.getMessage());
    }

    @Test
    void createOrderWithActiveDishSuccess() {
        List<OrderDish> orderDishesWithActiveDish = Arrays.asList(
            OrderDish.builder()
                .dishId(dishId1)
                .quantity(1)
                .build()
        );
        Order orderWithActiveDish = Order.builder()
            .restaurantId(restaurantId)
            .orderDishes(orderDishesWithActiveDish)
            .build();

        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("CUSTOMER");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(clientId);
        when(orderPersistencePort.hasActiveOrders(clientId)).thenReturn(false);
        when(restaurantServicePort.existsById(restaurantId)).thenReturn(true);
        when(dishServicePort.existsById(dishId1)).thenReturn(true);
        when(dishServicePort.isActiveById(dishId1)).thenReturn(true);
        when(orderPersistencePort.saveOrder(any(Order.class))).thenReturn(orderWithActiveDish);
        Order result = orderUseCase.createOrder(orderWithActiveDish);

        assertNotNull(result);
        assertEquals(clientId, orderWithActiveDish.getClientId());
        assertEquals(OrderStatus.PENDING, orderWithActiveDish.getStatus());
        assertNotNull(orderWithActiveDish.getDate());
        verify(orderPersistencePort).saveOrder(orderWithActiveDish);
        verify(dishServicePort).isActiveById(dishId1);
    }

    @Test
    void getOrdersByStatusAndRestaurantSuccess() {
        OrderStatus status = OrderStatus.PENDING;
        Pageable pageable = PageRequest.of(0, 10);
        List<Order> orderList = Arrays.asList(
            createTestOrder(restaurantId, status),
            createTestOrder(restaurantId, status)
        );
        Page<Order> expectedPage = new PageImpl<>(orderList, pageable, 2);
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("EMPLOYEE");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(UUID.randomUUID());
        when(userRoleValidationPort.getRestaurantIdByUserId(any(UUID.class))).thenReturn(Optional.of(restaurantId.toString()));
        when(orderPersistencePort.findByStatusAndRestaurant(status, restaurantId, pageable))
            .thenReturn(expectedPage);
        Page<Order> result = orderUseCase.getOrdersByStatusAndRestaurant(status, restaurantId, pageable);
        
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().stream().allMatch(oneOrder -> oneOrder.getStatus() == status && oneOrder.getRestaurantId().equals(restaurantId)));
        verify(orderPersistencePort).findByStatusAndRestaurant(status, restaurantId, pageable);
    }

    @Test
    void getOrdersByStatusAndRestaurantEmptyResult() {
        OrderStatus status = OrderStatus.CANCELLED;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("EMPLOYEE");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(UUID.randomUUID());
        when(userRoleValidationPort.getRestaurantIdByUserId(any(UUID.class))).thenReturn(Optional.of(restaurantId.toString()));
        when(orderPersistencePort.findByStatusAndRestaurant(status, restaurantId, pageable))
            .thenReturn(emptyPage);
        Page<Order> result = orderUseCase.getOrdersByStatusAndRestaurant(status, restaurantId, pageable);
        
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
        verify(orderPersistencePort).findByStatusAndRestaurant(status, restaurantId, pageable);
    }

    @Test
    void getOrdersByStatusAndRestaurantWithPagination() {
        OrderStatus status = OrderStatus.READY;
        Pageable pageable = PageRequest.of(1, 5);
        List<Order> orderList = Arrays.asList(
            createTestOrder(restaurantId, OrderStatus.READY)
        );
        Page<Order> expectedPage = new PageImpl<>(orderList, pageable, 6);
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("EMPLOYEE");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(UUID.randomUUID());
        when(userRoleValidationPort.getRestaurantIdByUserId(any(UUID.class))).thenReturn(Optional.of(restaurantId.toString()));
        when(orderPersistencePort.findByStatusAndRestaurant(status, restaurantId, pageable))
            .thenReturn(expectedPage);
        Page<Order> result = orderUseCase.getOrdersByStatusAndRestaurant(status, restaurantId, pageable);
        
        assertNotNull(result);
        assertEquals(6, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getNumber()); 
        assertEquals(2, result.getTotalPages());
        verify(orderPersistencePort).findByStatusAndRestaurant(status, restaurantId, pageable);
    }

    @Test
    void getOrdersByStatusAndRestaurantWithAdminRoleThrowsException() {
        OrderStatus status = OrderStatus.PENDING;
        Pageable pageable = PageRequest.of(0, 10);
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("ADMIN");
        
        OrderException exception = assertThrows(OrderException.class, () -> 
            orderUseCase.getOrdersByStatusAndRestaurant(status, restaurantId, pageable));
        
        assertEquals("You are not a EMPLOYEE", exception.getMessage());
    }

    @Test
    void getOrdersByStatusAndRestaurantUserNotFoundThrowsException() {
        OrderStatus status = OrderStatus.PENDING;
        Pageable pageable = PageRequest.of(0, 10);

        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("EMPLOYEE");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(employeeId);
        when(userRoleValidationPort.getRestaurantIdByUserId(employeeId)).thenReturn(Optional.empty());
        
        OrderException exception = assertThrows(OrderException.class, () -> 
            orderUseCase.getOrdersByStatusAndRestaurant(status, restaurantId, pageable));
        
        assertEquals("User not found or has no restaurant", exception.getMessage());
    }

    @Test
    void getOrdersByStatusAndRestaurantEmployeeNotOfRestaurantThrowsException() {
        OrderStatus status = OrderStatus.PENDING;
        Pageable pageable = PageRequest.of(0, 10);
        UUID differentRestaurantId = UUID.randomUUID();
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("EMPLOYEE");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(employeeId);
        when(userRoleValidationPort.getRestaurantIdByUserId(employeeId)).thenReturn(Optional.of(differentRestaurantId.toString()));
        
        OrderException exception = assertThrows(OrderException.class, () -> 
            orderUseCase.getOrdersByStatusAndRestaurant(status, restaurantId, pageable));
        
        assertEquals("User must be an employee of the restaurant", exception.getMessage());
    }

    @Test
    void getOrdersByStatusAndRestaurantWithEmployeeRoleSuccess() {
        OrderStatus status = OrderStatus.PENDING;
        Pageable pageable = PageRequest.of(0, 10);
        List<Order> orderList = Arrays.asList(
            createTestOrder(restaurantId, status)
        );
        Page<Order> expectedPage = new PageImpl<>(orderList, pageable, 1);
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("EMPLOYEE");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(employeeId);
        when(userRoleValidationPort.getRestaurantIdByUserId(employeeId)).thenReturn(Optional.of(restaurantId.toString()));
        when(orderPersistencePort.findByStatusAndRestaurant(status, restaurantId, pageable))
            .thenReturn(expectedPage);
        Page<Order> result = orderUseCase.getOrdersByStatusAndRestaurant(status, restaurantId, pageable);
        
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(orderPersistencePort).findByStatusAndRestaurant(status, restaurantId, pageable);
    }

    @Test
    void assignOrderToEmployeeSuccess() {
        Order orderTest = createTestOrder(restaurantId, OrderStatus.PENDING);
        Order updatedOrder = orderTest;
        updatedOrder.setChefId(null); 
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("EMPLOYEE");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(employeeId);
        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(orderTest));
        when(userRoleValidationPort.getRestaurantIdByUserId(employeeId)).thenReturn(Optional.of(restaurantId.toString()));
        when(orderPersistencePort.updateOrder(orderTest)).thenReturn(Optional.of(updatedOrder));
        Order result = orderUseCase.assignOrderToEmployee(orderId);

        assertNotNull(result);
        assertEquals(employeeId, result.getChefId());
        verify(securityContextPort).getRoleOfUserAutenticated();
        verify(securityContextPort).getUserIdOfUserAutenticated();
        verify(orderPersistencePort).findById(orderId);
        verify(userRoleValidationPort).getRestaurantIdByUserId(employeeId);
        verify(orderPersistencePort).updateOrder(orderTest);
    }

    @Test
    void assignOrderThrowsExceptionIfNotEmployee() {
        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(order));
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("CUSTOMER");
        
        OrderException exception = assertThrows(OrderException.class, () -> 
            orderUseCase.assignOrderToEmployee(orderId)
        );
        assertEquals("You are not a EMPLOYEE", exception.getMessage());
        verify(securityContextPort).getRoleOfUserAutenticated();
        verifyNoMoreInteractions(orderPersistencePort, userRoleValidationPort);
    }

    @Test
    void assignOrderThrowsExceptionIfOrderNotFound() {
        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.empty());

        OrderException exception = assertThrows(OrderException.class, () -> 
            orderUseCase.assignOrderToEmployee(orderId)
        );        
        assertEquals("Order not found", exception.getMessage());
        verify(orderPersistencePort).findById(orderId);
    }

    @Test
    void assignOrderThrowsExceptionIfUserNotEmployee() {
        Order orderTest = createTestOrder(restaurantId, OrderStatus.PENDING);
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("EMPLOYEE");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(employeeId);
        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(orderTest));
        when(userRoleValidationPort.getRestaurantIdByUserId(employeeId)).thenReturn(Optional.empty());

        OrderException exception = assertThrows(OrderException.class, () -> 
            orderUseCase.assignOrderToEmployee(orderId)
        );
        assertEquals("User not found or has no restaurant", exception.getMessage());
        verify(securityContextPort).getRoleOfUserAutenticated();
        verify(securityContextPort).getUserIdOfUserAutenticated();
        verify(orderPersistencePort).findById(orderId); 
        verify(userRoleValidationPort).getRestaurantIdByUserId(employeeId);
        verifyNoMoreInteractions(orderPersistencePort);
    }

    @Test
    void assignOrderToEmployeeNotOfRestaurantThrowsException() {
        UUID orderRestaurantId = UUID.randomUUID();
        UUID employeeRestaurantId = UUID.randomUUID();
        Order orderTest = createTestOrder(orderRestaurantId, OrderStatus.PENDING);
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("EMPLOYEE");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(employeeId);
        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(orderTest));
        when(userRoleValidationPort.getRestaurantIdByUserId(employeeId)).thenReturn(Optional.of(employeeRestaurantId.toString()));

        OrderException exception = assertThrows(OrderException.class, () -> 
            orderUseCase.assignOrderToEmployee(orderId)
        );
        assertEquals("User must be an employee of the restaurant", exception.getMessage());
        verify(securityContextPort).getRoleOfUserAutenticated();
        verify(securityContextPort).getUserIdOfUserAutenticated();
        verify(orderPersistencePort).findById(orderId);
        verify(userRoleValidationPort).getRestaurantIdByUserId(employeeId);
        verifyNoMoreInteractions(orderPersistencePort);
    }

    @Test
    void assignOrderToEmployeeOrderNotPendingThrowsException() {
        Order orderTest = createTestOrder(restaurantId, OrderStatus.IN_PREPARATION);
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("EMPLOYEE");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(employeeId);
        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(orderTest));
        when(userRoleValidationPort.getRestaurantIdByUserId(employeeId)).thenReturn(Optional.of(restaurantId.toString()));

        OrderException exception = assertThrows(OrderException.class, () -> 
            orderUseCase.assignOrderToEmployee(orderId)
        );
        assertEquals("Order must be in PENDING status to be assigned", exception.getMessage());
        verify(securityContextPort).getRoleOfUserAutenticated();
        verify(securityContextPort).getUserIdOfUserAutenticated();
        verify(orderPersistencePort).findById(orderId);
        verify(userRoleValidationPort).getRestaurantIdByUserId(employeeId);
        verifyNoMoreInteractions(orderPersistencePort);
    }

    @Test
    void assignOrderToEmployeeAlreadyAssignedThrowsException() {
        UUID existingChefId = UUID.randomUUID();
        Order orderTest = createTestOrder(restaurantId, OrderStatus.PENDING);
        orderTest.setChefId(existingChefId);
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("EMPLOYEE");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(employeeId);
        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(orderTest));
        when(userRoleValidationPort.getRestaurantIdByUserId(employeeId)).thenReturn(Optional.of(restaurantId.toString()));

        OrderException exception = assertThrows(OrderException.class, () -> 
            orderUseCase.assignOrderToEmployee(orderId)
        );
        assertEquals("Order is already assigned to another chef", exception.getMessage());
        verify(securityContextPort).getRoleOfUserAutenticated();
        verify(securityContextPort).getUserIdOfUserAutenticated();
        verify(orderPersistencePort).findById(orderId);
        verify(userRoleValidationPort).getRestaurantIdByUserId(employeeId);
        verifyNoMoreInteractions(orderPersistencePort);
    }

    @ParameterizedTest
    @ValueSource(strings = {"READY", "DELIVERED", "CANCELLED", "IN_PREPARATION"})
    void assignOrderToEmployeeWithNonPendingStatusThrowsException(String status) {
        OrderStatus orderStatus = OrderStatus.valueOf(status);
        Order orderTest = createTestOrder(restaurantId, orderStatus);
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("EMPLOYEE");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(employeeId);
        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(orderTest));
        when(userRoleValidationPort.getRestaurantIdByUserId(employeeId)).thenReturn(Optional.of(restaurantId.toString()));

        OrderException exception = assertThrows(OrderException.class, () -> 
            orderUseCase.assignOrderToEmployee(orderId)
        );
        assertEquals("Order must be in PENDING status to be assigned", exception.getMessage());
        verify(securityContextPort).getRoleOfUserAutenticated();
        verify(securityContextPort).getUserIdOfUserAutenticated();
        verify(orderPersistencePort).findById(orderId);
        verify(userRoleValidationPort).getRestaurantIdByUserId(employeeId);
        verifyNoMoreInteractions(orderPersistencePort);
    }

    @Test
    void assignOrderToEmployeeThrowsExceptionIfCannotUpdateOrder() {
        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(order));
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("EMPLOYEE");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(employeeId);
        when(userRoleValidationPort.getRestaurantIdByUserId(employeeId)).thenReturn(Optional.of(restaurantId.toString()));
        when(orderPersistencePort.updateOrder(order)).thenReturn(Optional.empty());

        OrderException exception = assertThrows(OrderException.class, () -> 
            orderUseCase.assignOrderToEmployee(orderId)
        );
        assertEquals("Failed to update order - order not found after update", exception.getMessage());
        verify(orderPersistencePort).findById(orderId);
    }
} 