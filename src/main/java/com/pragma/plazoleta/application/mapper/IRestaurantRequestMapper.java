package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.request.RestaurantRequest;
import com.pragma.plazoleta.domain.model.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IRestaurantRequestMapper {
    Restaurant toModel(RestaurantRequest dto);
} 