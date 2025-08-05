package com.pragma.plazoleta.infrastructure.output.rest.client;

import com.pragma.plazoleta.application.dto.response.RoleResponse;
import com.pragma.plazoleta.application.dto.response.UserResponse;
import com.pragma.plazoleta.infrastructure.output.rest.FeignClientConfig;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-user", url = "${ms-user.url}", configuration = FeignClientConfig.class)
public interface UserFeignClient {
    @GetMapping("/roles/{id}")
    RoleResponse getRoleById(@PathVariable("id") String id);

    @GetMapping("/users/{id}")
    UserResponse getUserById(@PathVariable("id") String id);
} 