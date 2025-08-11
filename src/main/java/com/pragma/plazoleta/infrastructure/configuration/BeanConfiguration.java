package com.pragma.plazoleta.infrastructure.configuration;

import com.pragma.plazoleta.domain.spi.*;
import com.pragma.plazoleta.domain.api.IDishServicePort;
import com.pragma.plazoleta.domain.api.IRestaurantServicePort;
import com.pragma.plazoleta.application.mapper.IEmployeeAverageTimeMapper;
import com.pragma.plazoleta.application.mapper.INotificationMapper;
import com.pragma.plazoleta.application.mapper.IOrderSummaryMapper;
import com.pragma.plazoleta.application.mapper.ITraceabilityGroupedMapper;
import com.pragma.plazoleta.application.mapper.ITraceabilityMapper;
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
import com.pragma.plazoleta.infrastructure.output.rest.adapter.NotificationRestClientAdapter;
import com.pragma.plazoleta.infrastructure.output.rest.adapter.TraceRestClientAdapter;
import com.pragma.plazoleta.infrastructure.output.rest.adapter.UserRoleRestClientAdapter;
import com.pragma.plazoleta.infrastructure.output.rest.client.NotificationFeignClient;
import com.pragma.plazoleta.infrastructure.output.rest.client.TraceFeignClient;
import com.pragma.plazoleta.infrastructure.output.rest.client.UserFeignClient;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.ICategoryRepository;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.IDishRepository;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.IOrderRepository;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.IOrderDishRepository;
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
    private final ITraceabilityMapper traceabilityMapper;
    private final IEmployeeAverageTimeMapper employeeAverageTimeMapper;
    private final ITraceabilityGroupedMapper traceabilityGroupedMapper;
    private final IOrderSummaryMapper orderSummaryMapper;

    private final JwtService jwtService;
    private final UserFeignClient userFeignClient;
    private final TraceFeignClient traceFeignClient;
    private final NotificationFeignClient messageFeignClient;
    private final INotificationMapper notificationMapper;

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
        return new RestaurantUseCase(restaurantPersistencePort(), userRoleValidationPort(), securityContextPort(), traceCommunicationPort());
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
    public INotificationPersistencePort messagePersistencePort() {
        return new NotificationRestClientAdapter(messageFeignClient, notificationMapper);
    }

    @Bean
    public ITraceCommunicationPort traceCommunicationPort() {
        return new TraceRestClientAdapter(traceFeignClient, traceabilityMapper, traceabilityGroupedMapper, orderSummaryMapper, employeeAverageTimeMapper);
    }

    @Bean
    public IOrderServicePort orderServicePort() {
        return new OrderUseCase(orderPersistencePort(), dishServicePort(), restaurantServicePort(), 
        securityContextPort(), userRoleValidationPort(), messagePersistencePort(), traceCommunicationPort(), orderStatusService());
    }
} 