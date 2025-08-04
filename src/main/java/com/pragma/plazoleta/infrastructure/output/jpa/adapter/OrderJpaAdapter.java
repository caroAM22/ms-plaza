package com.pragma.plazoleta.infrastructure.output.jpa.adapter;

import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderDish;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.domain.spi.IOrderPersistencePort;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.OrderEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.OrderDishEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.OrderStatusEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.mapper.IOrderEntityMapper;
import com.pragma.plazoleta.infrastructure.output.jpa.mapper.IOrderDishEntityMapper;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.IOrderRepository;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.IOrderDishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderJpaAdapter implements IOrderPersistencePort {
    
    private final IOrderRepository orderRepository;
    private final IOrderDishRepository orderDishRepository;
    private final IOrderEntityMapper orderEntityMapper;
    private final IOrderDishEntityMapper orderDishEntityMapper;

    @Override
    @Transactional
    public Order saveOrder(Order order) {
        OrderEntity orderEntity = orderEntityMapper.toOrderEntity(order);
        OrderEntity savedOrderEntity = orderRepository.save(orderEntity);
        List<OrderDishEntity> orderDishEntities = orderDishEntityMapper.toOrderDishEntityList(order.getOrderDishes());
        orderDishRepository.saveAll(orderDishEntities);
        
        return orderEntityMapper.toOrder(savedOrderEntity);
    }

    @Override
    public List<OrderDish> saveOrderDishes(List<OrderDish> orderDishes) {
        List<OrderDishEntity> orderDishEntities = orderDishEntityMapper.toOrderDishEntityList(orderDishes);
        List<OrderDishEntity> savedOrderDishEntities = orderDishRepository.saveAll(orderDishEntities);
        return orderDishEntityMapper.toOrderDishList(savedOrderDishEntities);
    }

    @Override
    public boolean hasActiveOrders(UUID clientId) {
        List<OrderStatusEntity> activeStatuses = Arrays.asList(
            OrderStatusEntity.PENDING,
            OrderStatusEntity.IN_PREPARATION,
            OrderStatusEntity.READY
        );
        return orderRepository.existsByClientIdAndStatusIn(clientId.toString(), activeStatuses);
    }

    @Override
    public Page<Order> findByStatusAndRestaurant(OrderStatus status, UUID restaurantId, Pageable pageable) {
        OrderStatusEntity statusEntity = OrderStatusEntity.valueOf(status.name());        
        Page<OrderEntity> orderEntities = orderRepository.findByStatusAndRestaurantId(statusEntity, restaurantId.toString(), pageable);
        return orderEntities.map(orderEntityMapper::toOrder);
    }

    @Override
    public java.util.Optional<Order> findById(UUID id) {
        return orderRepository.findById(id.toString())
                .map(orderEntityMapper::toOrder);
    }

    @Override
    @Transactional
    public Optional<Order> updateOrderStatusAndChefId(Order order) {
        OrderEntity orderEntity = orderEntityMapper.toOrderEntity(order);
        int updatedRows = orderRepository.updateChefId(orderEntity.getId(), orderEntity.getChefId(), orderEntity.getStatus());
        if (updatedRows == 0) {
            return Optional.empty();
        }
        return orderRepository.findById(orderEntity.getId())
                .map(orderEntityMapper::toOrder);
    }

    @Override
    @Transactional
    public Optional<Order> updateOrderStatusAndSecurityPin(Order order) {
        OrderEntity orderEntity = orderEntityMapper.toOrderEntity(order);
        int updatedRows = orderRepository.updateSecurityPin(orderEntity.getId(), orderEntity.getSecurityPin(), orderEntity.getStatus());
        if (updatedRows == 0) {
            return Optional.empty();
        }
        return orderRepository.findById(orderEntity.getId())
                .map(orderEntityMapper::toOrder);
    }

    @Override
    @Transactional
    public Optional<Order> updateOrderStatus(Order order) {
        OrderEntity orderEntity = orderEntityMapper.toOrderEntity(order);
        int updatedRows = orderRepository.updateOrderStatus(orderEntity.getId(), orderEntity.getStatus());
        if (updatedRows == 0) {
            return Optional.empty();
        }
        return orderRepository.findById(orderEntity.getId())
                .map(orderEntityMapper::toOrder);
    }
} 