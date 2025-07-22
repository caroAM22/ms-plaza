package com.pragma.plazoleta.application.handler;

import java.util.List;

import com.pragma.plazoleta.application.dto.response.CategoryResponse;

public interface ICategoryHandler {
    CategoryResponse getByName(String name);
    List<CategoryResponse> getAll();
} 