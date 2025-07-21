package com.pragma.plazoleta.infrastructure.output.restclient;

import lombok.Getter;
import lombok.Setter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-user", url = "${ms-user.url}")
public interface UserRoleFeignClient {
    @GetMapping("/users/{userId}/role")
    UserRoleResponse getUserRole(@PathVariable("userId") String userId);

    @Setter
    @Getter
    class UserRoleResponse {
        private String roleId;
        private String roleName;
    }
} 