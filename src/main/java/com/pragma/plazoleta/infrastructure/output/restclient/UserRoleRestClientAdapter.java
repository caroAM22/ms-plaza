package com.pragma.plazoleta.infrastructure.output.restclient;

import com.pragma.plazoleta.domain.exception.DomainException;
import com.pragma.plazoleta.domain.spi.IUserRoleValidationPort;
import com.pragma.plazoleta.infrastructure.output.restclient.dto.RoleResponse;
import com.pragma.plazoleta.infrastructure.output.restclient.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRoleRestClientAdapter implements IUserRoleValidationPort {

    private final UserFeignClient userFeignClient;

    @Override
    public boolean hasOwnerRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities() == null || auth.getAuthorities().isEmpty()) {
            throw new DomainException("No authentication or role found in token");
        }
        String role = auth.getAuthorities().iterator().next().getAuthority();
        return "OWNER".equalsIgnoreCase(role);
    }

    @Override
    public String getRoleNameByUserId(String userId) {
        try {
            UserResponse userResponse = userFeignClient.getUserById(userId);
            if (userResponse == null || userResponse.getRoleId() == null || userResponse.getRoleId().isEmpty()) {
                throw new DomainException("No role found for user");
            }
            RoleResponse roleResponse = userFeignClient.getRoleById(userResponse.getRoleId());
            if (roleResponse == null || roleResponse.getName() == null || roleResponse.getName().isEmpty()) {
                throw new DomainException("No role found");
            }
            return roleResponse.getName();
        } catch (Exception ex) {
            throw new DomainException("Not found (404)");
        }
    }
} 