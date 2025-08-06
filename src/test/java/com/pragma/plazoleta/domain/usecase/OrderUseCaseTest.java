package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.api.IDishServicePort;
import com.pragma.plazoleta.domain.api.IRestaurantServicePort;
import com.pragma.plazoleta.domain.exception.OrderException;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderDish;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.domain.model.Traceability;
import com.pragma.plazoleta.domain.model.TraceabilityGrouped;
import com.pragma.plazoleta.domain.spi.IOrderPersistencePort;
import com.pragma.plazoleta.domain.spi.ISecurityContextPort;
import com.pragma.plazoleta.domain.spi.ITracePersistencePort;

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
import com.pragma.plazoleta.domain.service.OrderStatusService;
import com.pragma.plazoleta.domain.model.Notification;
import com.pragma.plazoleta.domain.model.NotificationResult;
import com.pragma.plazoleta.domain.spi.INotificationPersistencePort;

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

    @Mock
    private INotificationPersistencePort messagePersistencePort;

    @Mock
    private ITracePersistencePort tracePersistencePort;

    @Mock
    private OrderStatusService orderStatusService;

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
        orders.setClientId(clientId);
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
        when(userRoleValidationPort.getEmailByUserId(any(UUID.class))).thenReturn(Optional.of("test@test.com"));
        when(tracePersistencePort.createTrace(any(Traceability.class))).thenReturn(Optional.of(new Traceability()));
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
        when(userRoleValidationPort.getEmailByUserId(any(UUID.class))).thenReturn(Optional.of("test@test.com"));
        when(tracePersistencePort.createTrace(any(Traceability.class))).thenReturn(Optional.of(new Traceability()));
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
        when(orderPersistencePort.updateOrderStatusAndChefId(orderTest)).thenReturn(Optional.of(updatedOrder));
        when(userRoleValidationPort.getEmailByUserId(employeeId)).thenReturn(Optional.of("employee@example.com"));
        when(userRoleValidationPort.getEmailByUserId(orderTest.getClientId())).thenReturn(Optional.of("client@example.com"));
        when(tracePersistencePort.createTrace(any(Traceability.class))).thenReturn(Optional.of(new Traceability()));
        Order result = orderUseCase.assignOrderToEmployee(orderId);

        assertNotNull(result);
        assertEquals(employeeId, result.getChefId());
        verify(securityContextPort).getRoleOfUserAutenticated();
        verify(securityContextPort).getUserIdOfUserAutenticated();
        verify(orderPersistencePort).findById(orderId);
        verify(userRoleValidationPort).getRestaurantIdByUserId(employeeId);
        verify(orderPersistencePort).updateOrderStatusAndChefId(orderTest);
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
        String expectedMessage = "Invalid status transition from " + OrderStatus.IN_PREPARATION + " to " + OrderStatus.IN_PREPARATION;
        OrderException expectedException = new OrderException(expectedMessage);
        
        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(orderTest));
        doThrow(expectedException).when(orderStatusService).validateStatusTransition(OrderStatus.IN_PREPARATION, OrderStatus.IN_PREPARATION);

        OrderException exception = assertThrows(OrderException.class, () -> 
            orderUseCase.assignOrderToEmployee(orderId)
        );
        assertEquals(expectedMessage, exception.getMessage());
        verify(orderPersistencePort).findById(orderId);
        verify(orderStatusService).validateStatusTransition(OrderStatus.IN_PREPARATION, OrderStatus.IN_PREPARATION);
        verifyNoMoreInteractions(orderPersistencePort);
    }

    @Test
    void assignOrderToEmployeeThrowsExceptionIfCannotUpdateOrder() {
        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(order));
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("EMPLOYEE");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(employeeId);
        when(userRoleValidationPort.getRestaurantIdByUserId(employeeId)).thenReturn(Optional.of(restaurantId.toString()));
        when(orderPersistencePort.updateOrderStatusAndChefId(order)).thenReturn(Optional.empty());

        OrderException exception = assertThrows(OrderException.class, () -> 
            orderUseCase.assignOrderToEmployee(orderId)
        );
        assertEquals("Failed to update order - order not found after update", exception.getMessage());
        verify(orderPersistencePort).findById(orderId);
    }

    @Test
    void updateSecurityPinOrderAlreadyHasPinThrowsException() {
        Order orderTest = createTestOrder(restaurantId, OrderStatus.IN_PREPARATION);
        orderTest.setChefId(employeeId);
        orderTest.setSecurityPin("123456");
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("EMPLOYEE");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(employeeId);
        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(orderTest));
        
        OrderException exception = assertThrows(OrderException.class, () -> 
            orderUseCase.updateSecurityPin(orderId)
        );
        assertEquals("Order already has a security PIN generated previously", exception.getMessage());
        verify(securityContextPort).getRoleOfUserAutenticated();
        verify(securityContextPort).getUserIdOfUserAutenticated();
        verify(orderPersistencePort).findById(orderId);
        verifyNoMoreInteractions(orderPersistencePort);
    }

    @Test
    void updateSecurityPinSuccessfully() {
        Order orderTest = createTestOrder(restaurantId, OrderStatus.IN_PREPARATION);
        orderTest.setChefId(employeeId);
        Order updatedOrder = createTestOrder(restaurantId, OrderStatus.READY);
        updatedOrder.setChefId(employeeId);
        updatedOrder.setSecurityPin("123456");
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("EMPLOYEE");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(employeeId);
        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(orderTest));
        when(orderPersistencePort.updateOrderStatusAndSecurityPin(orderTest)).thenReturn(Optional.of(updatedOrder));
        when(userRoleValidationPort.getEmailByUserId(employeeId)).thenReturn(Optional.of("employee@example.com"));
        when(userRoleValidationPort.getEmailByUserId(orderTest.getClientId())).thenReturn(Optional.of("client@example.com"));
        when(tracePersistencePort.createTrace(any(Traceability.class))).thenReturn(Optional.of(new Traceability()));
        Order result = orderUseCase.updateSecurityPin(orderId);
        
        assertNotNull(result);
        assertEquals(OrderStatus.READY, result.getStatus());
        assertNotNull(result.getSecurityPin());
        assertEquals(employeeId, result.getChefId());
        verify(securityContextPort).getRoleOfUserAutenticated();
        verify(securityContextPort).getUserIdOfUserAutenticated();
        verify(orderPersistencePort).findById(orderId);
        verify(orderPersistencePort).updateOrderStatusAndSecurityPin(orderTest);
    }

    @Test
    void updateSecurityPinUserNotChefThrowsException() {
        Order orderTest = createTestOrder(restaurantId, OrderStatus.IN_PREPARATION);
        orderTest.setChefId(UUID.randomUUID());
        orderTest.setSecurityPin(null);
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("EMPLOYEE");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(employeeId);
        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(orderTest));
        
        OrderException exception = assertThrows(OrderException.class, () -> 
            orderUseCase.updateSecurityPin(orderId)
        );
        assertEquals("User must be the chef of the order", exception.getMessage());
        verify(securityContextPort).getRoleOfUserAutenticated();
        verify(securityContextPort).getUserIdOfUserAutenticated();
        verify(orderPersistencePort).findById(orderId);
        verifyNoMoreInteractions(orderPersistencePort);
    }

    @Test
    void updateSecurityPinPersistenceReturnsEmptyThrowsException() {
        Order orderTest = createTestOrder(restaurantId, OrderStatus.IN_PREPARATION);
        orderTest.setChefId(employeeId);
        orderTest.setSecurityPin(null);
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("EMPLOYEE");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(employeeId);
        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(orderTest));
        when(orderPersistencePort.updateOrderStatusAndSecurityPin(orderTest)).thenReturn(Optional.empty());
        
        OrderException exception = assertThrows(OrderException.class, () -> 
            orderUseCase.updateSecurityPin(orderId)
        );
        assertEquals("Failed to update order status", exception.getMessage());
        verify(securityContextPort).getRoleOfUserAutenticated();
        verify(securityContextPort).getUserIdOfUserAutenticated();
        verify(orderPersistencePort).findById(orderId);
        verify(orderPersistencePort).updateOrderStatusAndSecurityPin(orderTest);
    }

    @Test
    void sendNotificationToCustomerSuccessfully() {
        Order orderTest = createTestOrder(restaurantId, OrderStatus.READY);
        orderTest.setChefId(employeeId);
        orderTest.setSecurityPin("123456");
        
        NotificationResult expectedResult = new NotificationResult("msg-123", "SENT", "2024-01-01T10:00:00");
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("EMPLOYEE");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(employeeId);
        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(orderTest));
        when(userRoleValidationPort.getPhoneNumberByUserId(orderTest.getClientId())).thenReturn(Optional.of("+573158796999"));
        when(messagePersistencePort.sendMessage(any(Notification.class))).thenReturn(Optional.of(expectedResult));
        
        NotificationResult result = orderUseCase.sendNotificationToCustomer(orderId);
        
        assertNotNull(result);
        assertEquals("msg-123", result.getMessage());
        assertEquals("SENT", result.getStatus());
        
        verify(securityContextPort).getRoleOfUserAutenticated();
        verify(securityContextPort).getUserIdOfUserAutenticated();
        verify(orderPersistencePort).findById(orderId);
        verify(userRoleValidationPort).getPhoneNumberByUserId(orderTest.getClientId());
        verify(messagePersistencePort).sendMessage(any(Notification.class));
    }

    @Test
    void sendNotificationToCustomerOrderNotReadyThrowsException() {
        Order orderTest = createTestOrder(restaurantId, OrderStatus.IN_PREPARATION);
        
        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(orderTest));
        
        OrderException exception = assertThrows(OrderException.class, () -> 
            orderUseCase.sendNotificationToCustomer(orderId)
        );
        assertEquals("Order is not in status ready", exception.getMessage());
        verify(orderPersistencePort).findById(orderId);
    }

    @Test
    void sendNotificationToCustomerClientPhoneNumberNotFoundThrowsException() {
        Order orderTest = createTestOrder(restaurantId, OrderStatus.READY);
        orderTest.setChefId(employeeId);
        orderTest.setSecurityPin("123456");
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("EMPLOYEE");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(employeeId);
        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(orderTest));
        when(userRoleValidationPort.getPhoneNumberByUserId(orderTest.getClientId())).thenReturn(Optional.empty());
        
        OrderException exception = assertThrows(OrderException.class, () -> 
            orderUseCase.sendNotificationToCustomer(orderId)
        );
        assertEquals("Client phone number not found", exception.getMessage());
        verify(securityContextPort).getRoleOfUserAutenticated();
        verify(securityContextPort).getUserIdOfUserAutenticated();
        verify(orderPersistencePort).findById(orderId);
        verify(userRoleValidationPort).getPhoneNumberByUserId(orderTest.getClientId());
        verifyNoMoreInteractions(messagePersistencePort);
    }

    @Test
    void sendNotificationToCustomerFailedToSendNotificationThrowsException() {
        Order orderTest = createTestOrder(restaurantId, OrderStatus.READY);
        orderTest.setChefId(employeeId);
        orderTest.setSecurityPin("123456");
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("EMPLOYEE");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(employeeId);
        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(orderTest));
        when(userRoleValidationPort.getPhoneNumberByUserId(orderTest.getClientId())).thenReturn(Optional.of("+573158796999"));
        when(messagePersistencePort.sendMessage(any(Notification.class))).thenReturn(Optional.empty());
        
        OrderException exception = assertThrows(OrderException.class, () -> 
            orderUseCase.sendNotificationToCustomer(orderId)
        );
        assertEquals("Failed to send notification", exception.getMessage());
        verify(securityContextPort).getRoleOfUserAutenticated();
        verify(securityContextPort).getUserIdOfUserAutenticated();
        verify(orderPersistencePort).findById(orderId);
        verify(userRoleValidationPort).getPhoneNumberByUserId(orderTest.getClientId());
        verify(messagePersistencePort).sendMessage(any(Notification.class));
    }

    @Test
    void updateOrderToDeliveredPersistenceReturnsEmptyThrowsException() {
        Order orderTest = createTestOrder(restaurantId, OrderStatus.READY);
        orderTest.setChefId(employeeId);
        orderTest.setSecurityPin("123456");
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("EMPLOYEE");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(employeeId);
        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(orderTest));
        when(orderPersistencePort.updateOrderStatus(orderTest)).thenReturn(Optional.empty());
        
        OrderException exception = assertThrows(OrderException.class, () -> 
            orderUseCase.updateOrderToDelivered(orderId, "123456")
        );
        assertEquals("Failed to update order status", exception.getMessage());
        verify(securityContextPort).getRoleOfUserAutenticated();
        verify(securityContextPort).getUserIdOfUserAutenticated();
        verify(orderPersistencePort).findById(orderId);
        verify(orderPersistencePort).updateOrderStatus(orderTest);
    }

    @Test
    void updateOrderToDeliveredSuccessfully() {
        Order orderTest = createTestOrder(restaurantId, OrderStatus.READY);
        orderTest.setChefId(employeeId);
        orderTest.setSecurityPin("123456");
        Order updatedOrder = createTestOrder(restaurantId, OrderStatus.DELIVERED);
        updatedOrder.setChefId(employeeId);
        updatedOrder.setSecurityPin("123456");
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("EMPLOYEE");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(employeeId);
        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(orderTest));
        when(orderPersistencePort.updateOrderStatus(orderTest)).thenReturn(Optional.of(updatedOrder));    
        when(userRoleValidationPort.getEmailByUserId(employeeId)).thenReturn(Optional.of("employee@example.com"));
        when(userRoleValidationPort.getEmailByUserId(orderTest.getClientId())).thenReturn(Optional.of("client@example.com"));
        when(tracePersistencePort.createTrace(any(Traceability.class))).thenReturn(Optional.of(new Traceability()));
        Order result = orderUseCase.updateOrderToDelivered(orderId, "123456");
    
        assertNotNull(result);
        assertEquals(OrderStatus.DELIVERED, result.getStatus());
        verify(orderPersistencePort).updateOrderStatus(orderTest);
    }

    @Test
    void updateOrderToDeliveredInvalidPinThrowsException() {
        Order orderTest = createTestOrder(restaurantId, OrderStatus.READY);
        orderTest.setChefId(employeeId);
        orderTest.setSecurityPin("123457");
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("EMPLOYEE");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(employeeId);
        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(orderTest));
        
        OrderException exception = assertThrows(OrderException.class, () -> 
            orderUseCase.updateOrderToDelivered(orderId, "123456")
        );
        assertEquals("Invalid PIN", exception.getMessage());
        verify(securityContextPort).getRoleOfUserAutenticated();
        verify(securityContextPort).getUserIdOfUserAutenticated();
        verify(orderPersistencePort).findById(orderId);
    }

    @Test
    void cancelOrderSuccessfully() {
        Order orderTest = createTestOrder(restaurantId, OrderStatus.PENDING);
        Order updatedOrder = createTestOrder(restaurantId, OrderStatus.CANCELLED);
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("CUSTOMER");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(clientId);
        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(orderTest));
        when(orderPersistencePort.updateOrderStatus(orderTest)).thenReturn(Optional.of(updatedOrder));
        when(userRoleValidationPort.getEmailByUserId(clientId)).thenReturn(Optional.of("client@example.com"));
        when(tracePersistencePort.createTrace(any(Traceability.class))).thenReturn(Optional.of(new Traceability()));
        Order result = orderUseCase.cancelOrder(orderId);

        assertNotNull(result);
        assertEquals(OrderStatus.CANCELLED, result.getStatus());
        verify(orderPersistencePort).updateOrderStatus(orderTest);
    }

    @Test
    void cancelOrderNotCustomerRoleThrowsException() {
        Order orderTest = createTestOrder(restaurantId, OrderStatus.PENDING);
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("CUSTOMER");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(UUID.randomUUID());
        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(orderTest));

        OrderException exception = assertThrows(OrderException.class, () -> 
            orderUseCase.cancelOrder(orderId)
        );
        assertEquals("You are not the owner of this order", exception.getMessage());
        verify(securityContextPort).getRoleOfUserAutenticated();
        verify(securityContextPort).getUserIdOfUserAutenticated();
    }

    @Test
    void cancelOrderPersistenceReturnsEmptyThrowsException() {
        Order orderTest = createTestOrder(restaurantId, OrderStatus.PENDING);
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("CUSTOMER");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(clientId);
        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(orderTest));
        when(orderPersistencePort.updateOrderStatus(orderTest)).thenReturn(Optional.empty());
        
        OrderException exception = assertThrows(OrderException.class, () -> 
            orderUseCase.cancelOrder(orderId)
        );
        assertEquals("Failed to update order status", exception.getMessage());
        verify(securityContextPort).getRoleOfUserAutenticated();
        verify(securityContextPort).getUserIdOfUserAutenticated();
        verify(orderPersistencePort).findById(orderId);
        verify(orderPersistencePort).updateOrderStatus(orderTest);
    }

    @Test
    void FailedToCreateTraceThrowsException() {
        Order orderTest = createTestOrder(restaurantId, OrderStatus.PENDING);
        Order updatedOrder = createTestOrder(restaurantId, OrderStatus.CANCELLED);
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("CUSTOMER");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(clientId);
        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(orderTest));
        when(orderPersistencePort.updateOrderStatus(orderTest)).thenReturn(Optional.of(updatedOrder));
        when(userRoleValidationPort.getEmailByUserId(clientId)).thenReturn(Optional.of("client@example.com"));
        when(tracePersistencePort.createTrace(any(Traceability.class))).thenReturn(Optional.empty());

        OrderException exception = assertThrows(OrderException.class, () -> 
            orderUseCase.cancelOrder(orderId)
        );
        assertEquals("Failed to create traceability", exception.getMessage());
        verify(tracePersistencePort).createTrace(any(Traceability.class));
        verify(orderPersistencePort).findById(orderId);
        verify(orderPersistencePort).updateOrderStatus(orderTest);
        verify(userRoleValidationPort).getEmailByUserId(clientId);
    }

    @Test
    void FailedToGetEmployeeEmailThrowsException() {
        Order orderTest = createTestOrder(restaurantId, OrderStatus.READY);
        orderTest.setChefId(employeeId);
        orderTest.setSecurityPin("123456");
        Order updatedOrder = orderTest;
        updatedOrder.setStatus(OrderStatus.DELIVERED);
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("EMPLOYEE");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(employeeId);
        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(orderTest));
        when(orderPersistencePort.updateOrderStatus(orderTest)).thenReturn(Optional.of(updatedOrder));
        when(userRoleValidationPort.getEmailByUserId(employeeId)).thenReturn(Optional.empty());

        OrderException exception = assertThrows(OrderException.class, () -> 
            orderUseCase.updateOrderToDelivered(orderId, "123456")
        );
        assertEquals("Employee email not found", exception.getMessage());
        verify(userRoleValidationPort).getEmailByUserId(employeeId);
        verify(orderPersistencePort).findById(orderId);
    }

    @Test
    void FailedToGetClientEmailThrowsException() {
        Order orderTest = createTestOrder(restaurantId, OrderStatus.PENDING);
        Order updatedOrder = createTestOrder(restaurantId, OrderStatus.CANCELLED);

        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("CUSTOMER");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(clientId);
        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(orderTest));
        when(orderPersistencePort.updateOrderStatus(orderTest)).thenReturn(Optional.of(updatedOrder));
        when(userRoleValidationPort.getEmailByUserId(orderTest.getClientId())).thenReturn(Optional.empty());

        OrderException exception = assertThrows(OrderException.class, () -> 
            orderUseCase.cancelOrder(orderId)
        );
        assertEquals("Client email not found", exception.getMessage());
        verify(userRoleValidationPort).getEmailByUserId(orderTest.getClientId());
    }

    @Test
    void getClientHistorySuccessfully() {
        List<TraceabilityGrouped> expectedTraceability = Arrays.asList(
            TraceabilityGrouped.builder()
                .orderId(UUID.randomUUID())
                .traceabilityList(Arrays.asList(
                    Traceability.builder()
                        .orderId(UUID.randomUUID())
                        .clientId(clientId)
                        .build()
                ))
                .build()
        );
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("CUSTOMER");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(clientId);
        when(tracePersistencePort.getTraceByClientId(clientId)).thenReturn(expectedTraceability);
        List<TraceabilityGrouped> result = orderUseCase.getClientHistory(clientId);
        
        assertEquals(expectedTraceability, result);
        verify(securityContextPort).getRoleOfUserAutenticated();
        verify(securityContextPort).getUserIdOfUserAutenticated();
        verify(tracePersistencePort).getTraceByClientId(clientId);
    }

    @Test
    void getClientHistoryThrowsExceptionWhenNotCustomer() {
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("EMPLOYEE");
        
        OrderException exception = assertThrows(OrderException.class, 
            () -> orderUseCase.getClientHistory(clientId));
        assertEquals("You are not a CUSTOMER", exception.getMessage());
        
        verify(securityContextPort).getRoleOfUserAutenticated();
        verifyNoInteractions(tracePersistencePort);
    }

    @Test
    void getClientHistoryThrowsExceptionWhenNotLoggedIn() {
        UUID differentClientId = UUID.randomUUID();
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("CUSTOMER");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(differentClientId);
        
        OrderException exception = assertThrows(OrderException.class, 
            () -> orderUseCase.getClientHistory(clientId));
        assertEquals("You are not the client", exception.getMessage());
        verify(securityContextPort).getRoleOfUserAutenticated();
        verify(securityContextPort).getUserIdOfUserAutenticated();
        verifyNoInteractions(tracePersistencePort);
    }

    @Test
    void getOrderTraceabilitySuccessfully() {
        Order orderTest = createTestOrder(restaurantId, OrderStatus.DELIVERED);
        List<Traceability> expectedTraceability = Arrays.asList(
            Traceability.builder()
                .orderId(orderId)
                .clientId(clientId)
                .build()
        );
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("CUSTOMER");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(clientId);
        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(orderTest));
        when(tracePersistencePort.getTraceByOrderId(orderId)).thenReturn(expectedTraceability);
        List<Traceability> result = orderUseCase.getOrderTraceability(orderId);
        
        assertEquals(expectedTraceability, result);
        verify(securityContextPort).getRoleOfUserAutenticated();
        verify(securityContextPort).getUserIdOfUserAutenticated();
        verify(orderPersistencePort).findById(orderId);
        verify(tracePersistencePort).getTraceByOrderId(orderId);
    }

    @Test
    void getOrderTraceabilityThrowsExceptionWhenNotCustomer() {
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("EMPLOYEE");

        OrderException exception = assertThrows(OrderException.class, 
            () -> orderUseCase.getOrderTraceability(orderId));
        assertEquals("You are not a CUSTOMER", exception.getMessage());
        
        verify(securityContextPort).getRoleOfUserAutenticated();
        verifyNoInteractions(orderPersistencePort, tracePersistencePort);
    }

    @Test
    void getOrderTraceabilityThrowsExceptionWhenNotLoggedIn() {
        UUID differentClientId = UUID.randomUUID();
        Order orderTest = createTestOrder(restaurantId, OrderStatus.DELIVERED);
        
        when(securityContextPort.getRoleOfUserAutenticated()).thenReturn("CUSTOMER");
        when(securityContextPort.getUserIdOfUserAutenticated()).thenReturn(differentClientId);
        when(orderPersistencePort.findById(orderId)).thenReturn(Optional.of(orderTest));

        OrderException exception = assertThrows(OrderException.class, 
            () -> orderUseCase.getOrderTraceability(orderId));
        assertEquals("You are not the owner of this order", exception.getMessage());
        verify(securityContextPort).getRoleOfUserAutenticated();
        verify(securityContextPort).getUserIdOfUserAutenticated();
        verify(orderPersistencePort).findById(orderId);
        verifyNoInteractions(tracePersistencePort);
    }
} 