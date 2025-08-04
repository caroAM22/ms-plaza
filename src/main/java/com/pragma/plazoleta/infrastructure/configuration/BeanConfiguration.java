package com.pragma.plazoleta.infrastructure.configuration;

import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazoleta.domain.spi.ISecurityContextPort;
import com.pragma.plazoleta.domain.spi.IUserRoleValidationPort;
import com.pragma.plazoleta.domain.spi.ICategoryPersistencePort;
import com.pragma.plazoleta.domain.spi.IDishPersistencePort;
import com.pragma.plazoleta.domain.spi.IMessagePersistencePort;
import com.pragma.plazoleta.domain.spi.IOrderPersistencePort;
import com.pragma.plazoleta.domain.api.IDishServicePort;
import com.pragma.plazoleta.domain.api.IRestaurantServicePort;
import com.pragma.plazoleta.domain.api.ICategoryServicePort;
import com.pragma.plazoleta.domain.api.IOrderServicePort;
import com.pragma.plazoleta.domain.usecase.RestaurantUseCase;
import com.pragma.plazoleta.domain.usecase.CategoryUseCase;
import com.pragma.plazoleta.domain.usecase.DishUseCase;
import com.pragma.plazoleta.domain.usecase.OrderUseCase;
import com.pragma.plazoleta.infrastructure.output.jpa.adapter.RestaurantJpaAdapter;
import com.pragma.plazoleta.infrastructure.output.jpa.adapter.SecurityContextAdapter;
import com.pragma.plazoleta.infrastructure.output.jpa.adapter.CategoryJpaAdapter;
import com.pragma.plazoleta.infrastructure.output.jpa.adapter.DishJpaAdapter;
import com.pragma.plazoleta.infrastructure.output.jpa.adapter.OrderJpaAdapter;
import com.pragma.plazoleta.infrastructure.output.jpa.mapper.IRestaurantEntityMapper;
import com.pragma.plazoleta.infrastructure.output.jpa.mapper.ICategoryEntityMapper;
import com.pragma.plazoleta.infrastructure.output.jpa.mapper.IDishEntityMapper;
import com.pragma.plazoleta.infrastructure.output.jpa.mapper.IOrderEntityMapper;
import com.pragma.plazoleta.infrastructure.output.jpa.mapper.IOrderDishEntityMapper;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.IRestaurantRepository;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.ICategoryRepository;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.IDishRepository;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.IOrderRepository;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.IOrderDishRepository;
import com.pragma.plazoleta.infrastructure.output.restclient.UserRoleRestClientAdapter;
import com.pragma.plazoleta.infrastructure.output.restclient.MessageRestClientAdapter;
import com.pragma.plazoleta.infrastructure.output.restclient.UserFeignClient;
import com.pragma.plazoleta.infrastructure.security.JwtService;
import com.pragma.plazoleta.domain.service.OrderStatusService;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {
    private final IRestaurantRepository restaurantRepository;
    private final IRestaurantEntityMapper restaurantEntityMapper;

    private final ICategoryRepository categoryRepository;
    private final IDishRepository dishRepository;
    private final IOrderRepository orderRepository;
    private final IOrderDishRepository orderDishRepository;

    private final ICategoryEntityMapper categoryEntityMapper;
    private final IDishEntityMapper dishEntityMapper;
    private final IOrderEntityMapper orderEntityMapper;
    private final IOrderDishEntityMapper orderDishEntityMapper;

    private final JwtService jwtService;
    private final UserFeignClient userFeignClient;

    @Bean
    public ISecurityContextPort securityContextPort() {
        return new SecurityContextAdapter(jwtService);
    }

    @Bean
    public IRestaurantPersistencePort restaurantPersistencePort() {
        return new RestaurantJpaAdapter(restaurantRepository, restaurantEntityMapper);
    }

    @Bean
    public ICategoryPersistencePort categoryPersistencePort() {
        return new CategoryJpaAdapter(categoryRepository, categoryEntityMapper);
    }

    @Bean
    public IDishPersistencePort dishPersistencePort() {
        return new DishJpaAdapter(dishRepository, dishEntityMapper);
    }

    @Bean
    public IUserRoleValidationPort userRoleValidationPort() {
        return new UserRoleRestClientAdapter(userFeignClient);
    }

    @Bean
    public IRestaurantServicePort restaurantServicePort() {
        return new RestaurantUseCase(restaurantPersistencePort(), userRoleValidationPort(), securityContextPort());
    }

    @Bean
    public ICategoryServicePort categoryServicePort() {
        return new CategoryUseCase(categoryPersistencePort());
    }

    @Bean
    public IOrderPersistencePort orderPersistencePort() {
        return new OrderJpaAdapter(orderRepository, orderDishRepository, orderEntityMapper, orderDishEntityMapper);
    }

    @Bean
    public IDishServicePort dishServicePort() {
         return new DishUseCase(dishPersistencePort(), restaurantServicePort(), categoryServicePort(), securityContextPort());
    }

    @Bean
    public OrderStatusService orderStatusService() {
        return new OrderStatusService();
    }

    @Bean
    public IMessagePersistencePort messagePersistencePort(MessageRestClientAdapter messageRestClientAdapter) {
        return messageRestClientAdapter;
    }

    @Bean
    public IOrderServicePort orderServicePort(IMessagePersistencePort messagePersistencePort) {
        return new OrderUseCase(orderPersistencePort(), dishServicePort(), restaurantServicePort(), securityContextPort(), userRoleValidationPort(), messagePersistencePort, orderStatusService());
    }
} 