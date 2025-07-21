package com.pragma.plazoleta.infrastructure.output.restclient;

import com.pragma.plazoleta.domain.exception.DomainException;
import com.pragma.plazoleta.domain.spi.IUserRoleValidationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRoleRestClientAdapter implements IUserRoleValidationPort {
    private final UserRoleFeignClient feignClient;

    @Override
    public boolean hasOwnerRole(String userId) {
        try {
            UserRoleFeignClient.UserRoleResponse response = feignClient.getUserRole(userId);
            return response != null && "OWNER".equalsIgnoreCase(response.getRoleName());
        } catch (Exception e) {
            throw new DomainException("User not found or does not have OWNER role");
        }
    }
} 