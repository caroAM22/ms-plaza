package com.pragma.plazoleta.application.handler;

import java.util.List;

import com.pragma.plazoleta.application.dto.response.CategoryResponse;

public interface ICategoryHandler {
    List<CategoryResponse> getAll();
} 