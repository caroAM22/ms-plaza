package com.pragma.plazoleta.domain.spi;

import java.util.Optional;
import java.util.UUID;

public interface IUserRoleValidationPort {
    Optional<String> getRoleNameByUserId(UUID userId);
} 