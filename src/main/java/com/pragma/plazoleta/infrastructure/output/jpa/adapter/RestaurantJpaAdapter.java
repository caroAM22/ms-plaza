package com.pragma.plazoleta.infrastructure.output.jpa.adapter;

import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.model.DomainPage;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.RestaurantEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.mapper.IRestaurantEntityMapper;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.IRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RestaurantJpaAdapter implements IRestaurantPersistencePort {
    private final IRestaurantRepository restaurantRepository;
    private final IRestaurantEntityMapper restaurantEntityMapper;

    @Override
    public Restaurant save(Restaurant restaurant) {
        RestaurantEntity entity = restaurantEntityMapper.toRestaurantEntity(restaurant);
        RestaurantEntity saved = restaurantRepository.save(entity);
        return restaurantEntityMapper.toRestaurant(saved);
    }

    @Override
    public boolean existsByNit(long nit) {
        return restaurantRepository.existsByNit(nit);
    }

    @Override
    public boolean existsByName(String name) {
        return restaurantRepository.existsByName(name);
    }

    @Override
    public Optional<Restaurant> findById(UUID id) {
        return restaurantRepository.findById(id.toString()).map(restaurantEntityMapper::toRestaurant);
    }

    @Override
    public boolean existsById(UUID id) {
        return restaurantRepository.existsById(id.toString());
    }

    @Override
    public DomainPage<Restaurant> findAll(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<RestaurantEntity> springPage = restaurantRepository.findAllOrderedByName(pageRequest);
        
        return DomainPage.<Restaurant>builder()
            .content(springPage.getContent().stream()
                .map(restaurantEntityMapper::toRestaurant)
                .toList())
            .pageNumber(springPage.getNumber())
            .pageSize(springPage.getSize())
            .totalElements(springPage.getTotalElements())
            .build();
    }
} 