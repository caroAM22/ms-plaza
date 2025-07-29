package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.response.RestaurantMenuResponse;
import com.pragma.plazoleta.domain.model.Dish;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IRestaurantMenuMapper {
    
    RestaurantMenuResponse toRestaurantMenuResponse(Dish dish);
    
    default Page<RestaurantMenuResponse> toRestaurantMenuResponsePage(Page<Dish> dishes) {
        return dishes.map(this::toRestaurantMenuResponse);
    }
} 