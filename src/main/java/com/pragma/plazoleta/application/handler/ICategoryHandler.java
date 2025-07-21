package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.category.CategoryResponseDto;
import java.util.List;

public interface ICategoryHandler {
    CategoryResponseDto getByName(String name);
    List<CategoryResponseDto> getAll();
} 