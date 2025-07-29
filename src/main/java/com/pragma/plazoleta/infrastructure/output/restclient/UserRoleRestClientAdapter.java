package com.pragma.plazoleta.infrastructure.output.restclient;

import com.pragma.plazoleta.domain.spi.IUserRoleValidationPort;
import com.pragma.plazoleta.infrastructure.output.restclient.dto.RoleResponse;
import com.pragma.plazoleta.infrastructure.output.restclient.dto.UserResponse;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRoleRestClientAdapter implements IUserRoleValidationPort {

    private final UserFeignClient userFeignClient;

    @Override
    public Optional<String> getRoleNameByUserId(UUID userId) {
        try {
            UserResponse userResponse = userFeignClient.getUserById(userId.toString());
            if (userResponse == null || userResponse.getRoleId() == null || userResponse.getRoleId().isEmpty()) {
                return Optional.empty();
            }
            RoleResponse roleResponse = userFeignClient.getRoleById(userResponse.getRoleId());
            if (roleResponse == null || roleResponse.getName() == null || roleResponse.getName().isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(roleResponse.getName());
        } catch (Exception ex) {
            return Optional.empty();
        }
    }
} 