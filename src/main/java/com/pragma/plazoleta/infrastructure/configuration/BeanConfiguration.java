package com.pragma.plazoleta.infrastructure.configuration;

import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazoleta.domain.spi.IUserRoleValidationPort;
import com.pragma.plazoleta.domain.spi.ICategoryPersistencePort;
import com.pragma.plazoleta.domain.spi.IDishPersistencePort;
import com.pragma.plazoleta.domain.api.IDishServicePort;
import com.pragma.plazoleta.domain.api.IRestaurantServicePort;
import com.pragma.plazoleta.domain.api.ICategoryServicePort;
import com.pragma.plazoleta.domain.usecase.RestaurantUseCase;
import com.pragma.plazoleta.domain.usecase.CategoryUseCase;
import com.pragma.plazoleta.domain.usecase.DishUseCase;
import com.pragma.plazoleta.infrastructure.output.jpa.adapter.RestaurantJpaAdapter;
import com.pragma.plazoleta.infrastructure.output.jpa.adapter.CategoryJpaAdapter;
import com.pragma.plazoleta.infrastructure.output.jpa.adapter.DishJpaAdapter;
import com.pragma.plazoleta.infrastructure.output.jpa.mapper.IRestaurantEntityMapper;
import com.pragma.plazoleta.infrastructure.output.jpa.mapper.ICategoryEntityMapper;
import com.pragma.plazoleta.infrastructure.output.jpa.mapper.IDishEntityMapper;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.IRestaurantRepository;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.ICategoryRepository;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.IDishRepository;
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
    public ICategoryPersistencePort categoryPersistencePort(ICategoryRepository repo, ICategoryEntityMapper mapper) {
        return new CategoryJpaAdapter(repo, mapper);
    }

    @Bean
    public IDishPersistencePort dishPersistencePort(IDishRepository repo, IDishEntityMapper mapper) {
        return new DishJpaAdapter(repo, mapper);
    }

    @Bean
    public IUserRoleValidationPort userRoleValidationPort(UserRoleRestClientAdapter adapter) {
        return adapter;
    }

    @Bean
    public IRestaurantServicePort restaurantServicePort(IRestaurantPersistencePort persistence, IUserRoleValidationPort userRoleValidationPort) {
        return new RestaurantUseCase(persistence, userRoleValidationPort);
    }

    @Bean
    public ICategoryServicePort categoryServicePort(ICategoryPersistencePort persistence) {
        return new CategoryUseCase(persistence);
    }

    @Bean
    public IDishServicePort dishServicePort(IDishPersistencePort persistence) {
        return new DishUseCase(persistence);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
} 