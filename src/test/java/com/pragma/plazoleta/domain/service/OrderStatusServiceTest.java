package com.pragma.plazoleta.domain.service;

import com.pragma.plazoleta.domain.exception.OrderException;
import com.pragma.plazoleta.domain.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class OrderStatusServiceTest {

    private OrderStatusService orderStatusService;

    @BeforeEach
    void setUp() {
        orderStatusService = new OrderStatusService();
    }

    @ParameterizedTest
    @MethodSource("validStatusTransitions")
    void validateStatusTransitionValidTransitionsShouldNotThrowException(OrderStatus currentStatus, OrderStatus newStatus) {
        assertDoesNotThrow(() -> 
            orderStatusService.validateStatusTransition(currentStatus, newStatus)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidStatusTransitions")
    void validateStatusTransitionInvalidTransitionsShouldThrowException(OrderStatus currentStatus, OrderStatus newStatus) {
        OrderException exception = assertThrows(OrderException.class, () -> 
            orderStatusService.validateStatusTransition(currentStatus, newStatus)
        );
        
        String expectedMessage = "Invalid status transition from " + currentStatus + " to " + newStatus;
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void canBeCancelledWhenStatusIsPending_ShouldReturnTrue() {
        assertTrue(orderStatusService.canBeCancelled(OrderStatus.PENDING));
    }

    @ParameterizedTest
    @ValueSource(strings = {"IN_PREPARATION", "READY", "DELIVERED", "CANCELLED"})
    void canBeCancelled_WhenStatusIsNotPending_ShouldReturnFalse(OrderStatus status) {
        assertFalse(orderStatusService.canBeCancelled(status));
    }

    private static Stream<Arguments> validStatusTransitions() {
        return Stream.of(
            Arguments.of(OrderStatus.PENDING, OrderStatus.IN_PREPARATION),
            Arguments.of(OrderStatus.IN_PREPARATION, OrderStatus.READY),
            Arguments.of(OrderStatus.READY, OrderStatus.DELIVERED),
            Arguments.of(OrderStatus.PENDING, OrderStatus.CANCELLED)
        );
    }

    private static Stream<Arguments> invalidStatusTransitions() {
        return Stream.of(
            Arguments.of(OrderStatus.PENDING, OrderStatus.READY),
            Arguments.of(OrderStatus.PENDING, OrderStatus.DELIVERED),
            Arguments.of(OrderStatus.PENDING, OrderStatus.PENDING),
            
            Arguments.of(OrderStatus.IN_PREPARATION, OrderStatus.PENDING),
            Arguments.of(OrderStatus.IN_PREPARATION, OrderStatus.DELIVERED),
            Arguments.of(OrderStatus.IN_PREPARATION, OrderStatus.CANCELLED),
            Arguments.of(OrderStatus.IN_PREPARATION, OrderStatus.IN_PREPARATION),
            
            Arguments.of(OrderStatus.READY, OrderStatus.PENDING),
            Arguments.of(OrderStatus.READY, OrderStatus.IN_PREPARATION),
            Arguments.of(OrderStatus.READY, OrderStatus.CANCELLED),
            Arguments.of(OrderStatus.READY, OrderStatus.READY),

            Arguments.of(OrderStatus.DELIVERED, OrderStatus.PENDING),
            Arguments.of(OrderStatus.DELIVERED, OrderStatus.IN_PREPARATION),
            Arguments.of(OrderStatus.DELIVERED, OrderStatus.READY),
            Arguments.of(OrderStatus.DELIVERED, OrderStatus.CANCELLED),
            Arguments.of(OrderStatus.DELIVERED, OrderStatus.DELIVERED),
         
            Arguments.of(OrderStatus.CANCELLED, OrderStatus.PENDING),
            Arguments.of(OrderStatus.CANCELLED, OrderStatus.IN_PREPARATION),
            Arguments.of(OrderStatus.CANCELLED, OrderStatus.READY),
            Arguments.of(OrderStatus.CANCELLED, OrderStatus.DELIVERED),
            Arguments.of(OrderStatus.CANCELLED, OrderStatus.CANCELLED)
        );
    }
} 