package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.response.DishResponse;
import com.pragma.plazoleta.domain.model.Dish;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IDishResponseMapper {
    DishResponse toDto(Dish model);
} 