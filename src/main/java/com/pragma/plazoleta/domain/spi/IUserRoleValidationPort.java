package com.pragma.plazoleta.domain.spi;

import java.util.Optional;
import java.util.UUID;

public interface IUserRoleValidationPort {
    Optional<String> getRoleNameByUserId(UUID userId);
    Optional<String> getRestaurantIdByUserId(UUID userId);
    Optional<String> getPhoneNumberByUserId(UUID userId);
    Optional<String> getEmailByUserId(UUID userId);
} 