package com.pragma.plazoleta.infrastructure.output.restclient;

import com.pragma.plazoleta.domain.exception.DomainException;
import com.pragma.plazoleta.domain.spi.IUserRoleValidationPort;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class UserRoleRestClientAdapter implements IUserRoleValidationPort {
    private final RestTemplate restTemplate;

    @Value("${ms-user.url}")
    private String msUserUrl;

    @Value("${ms-user.role-endpoint}")
    private String roleEndpoint;

    @Override
    public boolean hasOwnerRole(String userId) {
        String url = msUserUrl + roleEndpoint.replace("{userId}", userId);
        try {
            UserRoleResponse response = restTemplate.getForObject(url, UserRoleResponse.class);
            return response != null && "OWNER".equalsIgnoreCase(response.getRoleName());
        } catch (HttpClientErrorException.NotFound e) {
            throw new DomainException("User not found or does not have OWNER role");
        } catch (HttpClientErrorException e) {
            throw new DomainException("Error validating user role: " + e.getStatusCode());
        } catch (Exception e) {
            throw new DomainException("Unexpected error validating user role");
        }
    }

    @Data
    public static class UserRoleResponse {
        private String roleId;
        private String roleName;
    }
} 