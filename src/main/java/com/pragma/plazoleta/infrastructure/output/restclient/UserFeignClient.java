package com.pragma.plazoleta.infrastructure.output.restclient;

import com.pragma.plazoleta.infrastructure.output.restclient.dto.response.RoleResponse;
import com.pragma.plazoleta.infrastructure.output.restclient.dto.response.UserResponse;

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