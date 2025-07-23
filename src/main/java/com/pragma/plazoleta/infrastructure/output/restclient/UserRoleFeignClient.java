package com.pragma.plazoleta.infrastructure.output.restclient;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-user", url = "${ms-user.url}")
public interface UserRoleFeignClient {
    @GetMapping("/users/{userId}")
    UserResponse getUser(@PathVariable("userId") String userId);

    @Data
    class UserResponse {
        private String id;
        private String name;
        private String lastname;
        private String email;
        private String phone;
        private String birthDate;
        private RoleResponse role;

        @Data
        public static class RoleResponse {
            private String roleId;
            private String roleName;
        }
    }
} 