package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.api.IOrderServicePort;
import com.pragma.plazoleta.domain.api.IDishServicePort;
import com.pragma.plazoleta.domain.api.IRestaurantServicePort;
import com.pragma.plazoleta.domain.exception.OrderException;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.DomainPage;
import com.pragma.plazoleta.domain.model.OrderDish;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.domain.model.Traceability;
import com.pragma.plazoleta.domain.model.TraceabilityGrouped;
import com.pragma.plazoleta.domain.model.Notification;
import com.pragma.plazoleta.domain.model.NotificationResult;
import com.pragma.plazoleta.domain.service.OrderStatusService;
import com.pragma.plazoleta.domain.spi.IOrderPersistencePort;
import com.pragma.plazoleta.domain.spi.ISecurityContextPort;
import com.pragma.plazoleta.domain.spi.ITraceCommunicationPort; 
import com.pragma.plazoleta.domain.spi.IUserRoleValidationPort;
import com.pragma.plazoleta.domain.utils.Constants;
import com.pragma.plazoleta.domain.spi.INotificationPersistencePort;

import java.security.SecureRandom;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public class OrderUseCase implements IOrderServicePort {
    private final SecureRandom random = new SecureRandom();
    private final IOrderPersistencePort orderPersistencePort;
    private final IDishServicePort dishServicePort;
    private final IRestaurantServicePort restaurantServicePort;
    private final ISecurityContextPort securityContextPort;
    private final IUserRoleValidationPort userRoleValidationPort;
    private final INotificationPersistencePort messagePersistencePort;
    private final ITraceCommunicationPort traceCommunicationPort;
    private final OrderStatusService orderStatusService;


    private Order getOrderById(UUID orderId) {
        return orderPersistencePort.findById(orderId)
                .orElseThrow(() -> new OrderException("Order not found"));
    }

    @Override
    public Order createOrder(Order orderToSave) {
        validateRole(Constants.CUSTOMER_ROLE);
        validateOrderDishes(orderToSave);
        UUID orderId = UUID.randomUUID();
        Order order= Order.builder()
                .id(orderId)
                .clientId(securityContextPort.getUserIdOfUserAutenticated())
                .date(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .restaurantId(orderToSave.getRestaurantId())
                .orderDishes(
                        orderToSave.getOrderDishes().stream()
                                .map(orderDish -> {
                                    orderDish.setOrderId(orderId);
                                    return orderDish;
                                })
                                .toList())
                .build();
        validateNoDuplicateDishes(order);
        validateNoActiveOrders(order);
        validateRestaurantExists(order);
        validateOrderDishesDetails(order);
        createTraceability(order, null, OrderStatus.PENDING.toString());
        return orderPersistencePort.saveOrder(order);
    }

    @Override
    public boolean hasActiveOrders(UUID clientId) {
        return orderPersistencePort.hasActiveOrders(clientId);
    }

    @Override
    public DomainPage<Order> getOrdersByStatusAndRestaurant(String status, UUID restaurantId, int page, int size) {
        OrderStatus orderStatus = OrderStatus.fromString(status);
        validateRole(Constants.EMPLOYEE_ROLE);
        validateEmployeeOfRestaurant(restaurantId, securityContextPort.getUserIdOfUserAutenticated());
        return orderPersistencePort.findByStatusAndRestaurant(orderStatus, restaurantId, page, size);
    }

    @Override
    public Order assignOrderToEmployee(UUID orderId) {
        Order order = getOrderById(orderId);
        orderStatusService.validateStatusTransition(order.getStatus(), OrderStatus.IN_PREPARATION);
        validateRole(Constants.EMPLOYEE_ROLE);
        UUID employeeId = securityContextPort.getUserIdOfUserAutenticated();
        validateEmployeeOfRestaurant(order.getRestaurantId(), employeeId);
        order.setChefId(employeeId);
        order.setStatus(OrderStatus.IN_PREPARATION);
        Optional<Order> updatedOrder = orderPersistencePort.updateOrderStatusAndChefId(order);
        if (updatedOrder.isEmpty()) {
            throw new OrderException("Failed to update order - order not found after update");
        }
        createTraceability(order, OrderStatus.PENDING.toString(), OrderStatus.IN_PREPARATION.toString());
        return updatedOrder.get();
    }

    @Override
    public Order updateSecurityPin(UUID orderId) {
        Order order = getOrderById(orderId);
        orderStatusService.validateStatusTransition(order.getStatus(), OrderStatus.READY);
        validateRole(Constants.EMPLOYEE_ROLE);
        validateEmployeeIsChef(order);
        if (order.getSecurityPin() != null) {
            throw new OrderException("Order already has a security PIN generated previously");
        }
        order.setSecurityPin(generateSecurityPin());
        order.setStatus(OrderStatus.READY);
        Optional<Order> updatedOrder = orderPersistencePort.updateOrderStatusAndSecurityPin(order);
        if (updatedOrder.isEmpty()) {
            throw new OrderException("Failed to update order status");
        }
        createTraceability(order, OrderStatus.IN_PREPARATION.toString(), OrderStatus.READY.toString());
        return updatedOrder.get();
    }

    @Override
    public NotificationResult sendNotificationToCustomer(UUID orderId) {
        Order order = getOrderById(orderId);
        if (order.getStatus() != OrderStatus.READY) {
            throw new OrderException("Order is not in status ready");
        }
        validateRole(Constants.EMPLOYEE_ROLE);
        validateEmployeeIsChef(order);
        Optional<String> phoneNumber = userRoleValidationPort.getPhoneNumberByUserId(order.getClientId());
        if (phoneNumber.isEmpty()) {
            throw new OrderException("Client phone number not found");
        }
            
        Notification notification = Notification.builder()
            .message("¡Tu pedido está listo! Código de seguridad: " + order.getSecurityPin() + ". Puedes recogerlo en el restaurante.")
            .phoneNumber(phoneNumber.get())
            .build();
            
        Optional<NotificationResult> result = messagePersistencePort.sendMessage(notification);
        if (result.isEmpty()) {
            throw new OrderException("Failed to send notification");
        }
        return result.get();
    }

    @Override
    public Order updateOrderToDelivered(UUID orderId, String pin) {
        Order order = getOrderById(orderId);
        orderStatusService.validateStatusTransition(order.getStatus(), OrderStatus.DELIVERED);
        validateRole(Constants.EMPLOYEE_ROLE);
        validateEmployeeIsChef(order);
        if (!order.getSecurityPin().equals(pin)) {
            throw new OrderException("Invalid PIN");
        }
        order.setStatus(OrderStatus.DELIVERED);
        Optional<Order> updatedOrder = orderPersistencePort.updateOrderStatus(order);
        if (updatedOrder.isEmpty()) {
            throw new OrderException("Failed to update order status");
        }
        createTraceability(order, OrderStatus.READY.toString(), OrderStatus.DELIVERED.toString());
        return updatedOrder.get();
    }

    @Override
    public Order cancelOrder(UUID orderId) {
        Order order = getOrderById(orderId);
        orderStatusService.validateStatusTransition(order.getStatus(), OrderStatus.CANCELLED);
        validateRole(Constants.CUSTOMER_ROLE);
        validateClientIsOrderOwner(order);
        order.setStatus(OrderStatus.CANCELLED);
        Optional<Order> updatedOrder = orderPersistencePort.updateOrderStatus(order);
        if (updatedOrder.isEmpty()) {
            throw new OrderException("Failed to update order status");
        }
        createTraceability(order, OrderStatus.PENDING.toString(), OrderStatus.CANCELLED.toString());
        return updatedOrder.get();
    }

    @Override
    public List<TraceabilityGrouped> getClientHistory(UUID clientId) {
        validateRole(Constants.CUSTOMER_ROLE);
        validateClientIsLoggedIn(clientId);
        return traceCommunicationPort.getTraceByClientId(clientId);
    }

    @Override
    public List<Traceability> getOrderTraceability(UUID orderId) {
        validateRole(Constants.CUSTOMER_ROLE);
        validateClientIsOrderOwner(getOrderById(orderId));
        return traceCommunicationPort.getTraceByOrderId(orderId);
    }

    private void createTraceability(Order order, String previousState, String newState) {
        Optional<String> employeeEmail = Optional.empty();
        if (order.getStatus() != OrderStatus.CANCELLED && order.getStatus() != OrderStatus.PENDING) {
            employeeEmail = userRoleValidationPort.getEmailByUserId(order.getChefId());
            if (employeeEmail.isEmpty()) {
                throw new OrderException("Employee email not found");
            } else {
                employeeEmail = Optional.of(employeeEmail.get());
            }
        }
        Optional<String> email = userRoleValidationPort.getEmailByUserId(order.getClientId());
        if (email.isEmpty()) {
            throw new OrderException("Client email not found");
        }
        Traceability traceability = Traceability.builder()
            .orderId(order.getId())
            .employeeId(order.getChefId())
            .clientId(order.getClientId())
            .clientEmail(email.get())
            .employeeEmail(employeeEmail.orElse(null))
            .previousState(previousState)
            .newState(newState)
            .restaurantId(order.getRestaurantId())
            .build();
        Optional<Traceability> traceabilityOptional = traceCommunicationPort.createTrace(traceability);
        if (traceabilityOptional.isEmpty()) {
            throw new OrderException("Failed to create traceability");
        }
    }

    private void validateClientIsOrderOwner(Order order) {
        if (!order.getClientId().equals(securityContextPort.getUserIdOfUserAutenticated())) {
            throw new OrderException("You are not the owner of this order");
        }
    }

    private void validateClientIsLoggedIn(UUID clientId) {
        if (!clientId.equals(securityContextPort.getUserIdOfUserAutenticated())) {
            throw new OrderException("You are not the client");
        }
    }

    private String generateSecurityPin() {
        return String.format("%06d", random.nextInt(1000000));
    }

    private void validateEmployeeOfRestaurant(UUID restaurantId, UUID employeeId) {
        String restaurantIdFromUser = userRoleValidationPort.getRestaurantIdByUserId(employeeId)
                .orElseThrow(() -> new OrderException("User not found or has no restaurant"));
        if (!restaurantIdFromUser.equals(restaurantId.toString())) {
            throw new OrderException("User must be an employee of the restaurant");
        }
    }

    private void validateEmployeeIsChef(Order order) {
        if (!order.getChefId().equals(securityContextPort.getUserIdOfUserAutenticated())) {
            throw new OrderException("User must be the chef of the order");
        }
    }

    private void validateRole(String role) {
        if (!role.equals(securityContextPort.getRoleOfUserAutenticated())) {
            throw new OrderException("You are not a " + role);
        }
    }

    private void validateOrderDishes(Order order) {
        if (order.getOrderDishes() == null || order.getOrderDishes().isEmpty()) {
            throw new OrderException("Order must contain at least one dish");
        }
    }

    private void validateNoDuplicateDishes(Order order) {
        Set<UUID> dishIds = new HashSet<>();
        for (OrderDish orderDish : order.getOrderDishes()) {
            if (!dishIds.add(orderDish.getDishId())) {
                throw new OrderException("Duplicate dish with id " + orderDish.getDishId() + " found in order");
            }
        }
    }

    private void validateNoActiveOrders(Order order) {
        if (hasActiveOrders(order.getClientId())) {
            throw new OrderException("You cannot have more than one active order");
        }
    }

    private void validateRestaurantExists(Order order) {
        if (!restaurantServicePort.existsById(order.getRestaurantId())) {
            throw new OrderException("Restaurant does not exist");
        }
    }

    private void validateOrderDishesDetails(Order order) {
        for (OrderDish orderDish : order.getOrderDishes()) {
            validateDishExists(orderDish);
            validateDishIsActive(orderDish);
            validateDishQuantity(orderDish);
        }
    }

    private void validateDishExists(OrderDish orderDish) {
        if (orderDish.getDishId() == null) {
            throw new OrderException("Dish id is required");
        }
        if (!dishServicePort.existsById(orderDish.getDishId())) {
            throw new OrderException("Dish with id " + orderDish.getDishId() + " does not exist");
        }
    }

    private void validateDishIsActive(OrderDish orderDish) {
        if (!dishServicePort.isActiveById(orderDish.getDishId())) {
            throw new OrderException("Dish with id " + orderDish.getDishId() + " is not active");
        }
    }

    private void validateDishQuantity(OrderDish orderDish) {
        if (orderDish.getQuantity() == null){
            throw new OrderException("Quantity is required");
        }
        if (orderDish.getQuantity() <= 0) {
            throw new OrderException("Quantity must be greater than 0");
        }
    }
} 