package com.pragma.plazoleta.infrastructure.configuration;

import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazoleta.domain.spi.IUserRoleValidationPort;
import com.pragma.plazoleta.domain.usecase.RestaurantUseCase;
import com.pragma.plazoleta.infrastructure.output.jpa.adapter.RestaurantJpaAdapter;
import com.pragma.plazoleta.infrastructure.output.jpa.mapper.IRestaurantEntityMapper;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.IRestaurantRepository;
import com.pragma.plazoleta.infrastructure.output.restclient.UserRoleRestClientAdapter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfiguration {
    @Bean
    public IRestaurantPersistencePort restaurantPersistencePort(IRestaurantRepository repo, IRestaurantEntityMapper mapper) {
        return new RestaurantJpaAdapter(repo, mapper);
    }

    @Bean
    public IUserRoleValidationPort userRoleValidationPort(UserRoleRestClientAdapter adapter) {
        return adapter;
    }

    @Bean
    public RestaurantUseCase restaurantUseCase(IRestaurantPersistencePort persistence, IUserRoleValidationPort userRoleValidationPort) {
        return new RestaurantUseCase(persistence, userRoleValidationPort);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
} 