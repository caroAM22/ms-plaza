package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.api.IOrderServicePort;
import com.pragma.plazoleta.domain.api.IDishServicePort;
import com.pragma.plazoleta.domain.api.IRestaurantServicePort;
import com.pragma.plazoleta.domain.exception.OrderException;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderDish;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.domain.spi.IOrderPersistencePort;
import com.pragma.plazoleta.domain.spi.ISecurityContextPort;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderUseCase implements IOrderServicePort {
    private final IOrderPersistencePort orderPersistencePort;
    private final IDishServicePort dishServicePort;
    private final IRestaurantServicePort restaurantServicePort;
    private final ISecurityContextPort securityContextPort;

    @Override
    public Order createOrder(Order order) {
        validateCustomerRole();
        setOrderClientId(order);
        validateOrderDishes(order);
        validateNoDuplicateDishes(order);
        validateNoActiveOrders(order);
        validateRestaurantExists(order);
        validateOrderDishesDetails(order);
        prepareOrderForSaving(order);
        
        return orderPersistencePort.saveOrder(order);
    }

    @Override
    public boolean hasActiveOrders(UUID clientId) {
        return orderPersistencePort.hasActiveOrders(clientId);
    }

    private void validateCustomerRole() {
        if (!"CUSTOMER".equals(securityContextPort.getRoleOfUserAutenticated())) {
            throw new OrderException("You are not a customer");
        }
    }

    private void setOrderClientId(Order order) {
        order.setClientId(securityContextPort.getUserIdOfUserAutenticated());
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

    private void prepareOrderForSaving(Order order) {
        order.setDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.getOrderDishes().forEach(orderDish -> orderDish.setOrderId(order.getId()));
    }
} 