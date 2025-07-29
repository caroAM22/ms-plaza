package com.pragma.plazoleta.infrastructure.output.jpa.mapper;

import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {IOrderDishEntityMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IOrderEntityMapper {
    
    Order toOrder(OrderEntity orderEntity);
    
    OrderEntity toOrderEntity(Order order);
} 