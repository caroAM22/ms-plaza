package com.pragma.plazoleta.application.handler.impl;

import com.pragma.plazoleta.application.dto.request.RestaurantRequestDto;
import com.pragma.plazoleta.application.dto.response.RestaurantResponseDto;
import com.pragma.plazoleta.application.mapper.IRestaurantRequestMapper;
import com.pragma.plazoleta.application.mapper.IRestaurantResponseMapper;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.usecase.RestaurantUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestaurantHandlerTest {
    private RestaurantUseCase useCase;
    private IRestaurantRequestMapper requestMapper;
    private IRestaurantResponseMapper responseMapper;
    private RestaurantHandler handler;

    @BeforeEach
    void setUp() {
        useCase = mock(RestaurantUseCase.class);
        requestMapper = mock(IRestaurantRequestMapper.class);
        responseMapper = mock(IRestaurantResponseMapper.class);
        handler = new RestaurantHandler(useCase, requestMapper, responseMapper);
    }

    @Test
    void createRestaurant() {
        RestaurantRequestDto dto = new RestaurantRequestDto();
        dto.setName("Qbano");
        dto.setNit(1234L);
        dto.setAddress("address");
        dto.setPhone("+573000000000");
        dto.setLogoUrl("logo");
        dto.setOwnerId("owner");
        Restaurant model = new Restaurant(null, "Qbano", 1234L, "address", "+573000000000", "logo", "owner");
        Restaurant saved = new Restaurant(UUID.randomUUID().toString(), "Qbano", 1234L, "address", "+573000000000", "logo", "owner");
        RestaurantResponseDto responseDto = new RestaurantResponseDto();
        responseDto.setName("Qbano");
        
        when(requestMapper.toModel(dto)).thenReturn(model);
        when(useCase.createRestaurant(any(Restaurant.class))).thenReturn(saved);
        when(responseMapper.toDto(saved)).thenReturn(responseDto);
        RestaurantResponseDto result = handler.createRestaurant(dto);
        
        assertEquals("Qbano", result.getName());
    }

    @Test
    void createRestaurantGeneratesIdIfNull() {
        RestaurantRequestDto dto = new RestaurantRequestDto();
        dto.setName("Qbano");
        dto.setNit(1234L);
        dto.setAddress("address");
        dto.setPhone("+573000000000");
        dto.setLogoUrl("logo");
        dto.setOwnerId("owner");
        Restaurant model = new Restaurant(null, "Qbano", 1234L, "address", "+573000000000", "logo", "owner");
        
        when(requestMapper.toModel(dto)).thenReturn(model);
        when(useCase.createRestaurant(any(Restaurant.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(responseMapper.toDto(any(Restaurant.class))).thenReturn(new RestaurantResponseDto());
        handler.createRestaurant(dto);
        
        assertNotNull(model.getId());
        assertDoesNotThrow(() -> UUID.fromString(model.getId()));
    }
} 