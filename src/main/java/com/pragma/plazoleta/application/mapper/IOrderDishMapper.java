package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.request.OrderDishRequest;
import com.pragma.plazoleta.application.dto.response.OrderDishResponse;
import com.pragma.plazoleta.domain.model.OrderDish;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IOrderDishMapper {
    
    @Mapping(target = "orderId", ignore = true)
    OrderDish toOrderDish(OrderDishRequest orderDishRequest);
    
    OrderDishResponse toOrderDishResponse(OrderDish orderDish);
    
    @Named("toOrderDishList")
    List<OrderDish> toOrderDishList(List<OrderDishRequest> orderDishRequests);
    
    List<OrderDishResponse> toOrderDishResponseList(List<OrderDish> orderDishes);
} 