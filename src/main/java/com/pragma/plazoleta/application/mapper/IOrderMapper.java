package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.request.OrderRequest;
import com.pragma.plazoleta.application.dto.response.OrderResponse;
import com.pragma.plazoleta.domain.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {IOrderDishMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IOrderMapper {
    
    @Mapping(target = "clientId", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "chefId", ignore = true)
    @Mapping(target = "orderDishes", source = "dishes", qualifiedByName = "toOrderDishList")
    Order toOrder(OrderRequest orderRequest);
    
    @Mapping(target = "status", expression = "java(order.getStatus().name())")
    @Mapping(target = "chefId", expression = "java(order.getChefId() != null ? order.getChefId().toString() : null)")
    OrderResponse toOrderResponse(Order order);
} 