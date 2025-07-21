package com.pragma.plazoleta.domain.spi;

public interface IUserRoleValidationPort {
    boolean hasOwnerRole(String userId);
} 