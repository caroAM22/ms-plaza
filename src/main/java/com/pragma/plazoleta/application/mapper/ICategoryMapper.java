package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.response.CategoryResponse;
import com.pragma.plazoleta.domain.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ICategoryMapper {
    CategoryResponse toCategoryResponse(Category category);
} 