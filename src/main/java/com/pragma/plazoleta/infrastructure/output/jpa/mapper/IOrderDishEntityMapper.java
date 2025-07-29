package com.pragma.plazoleta.infrastructure.output.jpa.mapper;

import com.pragma.plazoleta.domain.model.OrderDish;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.OrderDishEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IOrderDishEntityMapper {
    
    @Mapping(target = "orderId", source = "id.orderId")
    @Mapping(target = "dishId", source = "id.dishId")
    OrderDish toOrderDish(OrderDishEntity orderDishEntity);
    
    @Mapping(target = "id.orderId", source = "orderId")
    @Mapping(target = "id.dishId", source = "dishId")
    @Mapping(target = "order", ignore = true)
    OrderDishEntity toOrderDishEntity(OrderDish orderDish);
    
    List<OrderDish> toOrderDishList(List<OrderDishEntity> orderDishEntities);
    
    List<OrderDishEntity> toOrderDishEntityList(List<OrderDish> orderDishes);
} 