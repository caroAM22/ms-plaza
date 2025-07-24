package com.pragma.plazoleta.domain.spi;

public interface IUserRoleValidationPort {
    boolean hasOwnerRole(String userId);
    String getRoleNameByUserId(String userId);
} 