package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.exception.DomainException;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazoleta.domain.spi.IUserRoleValidationPort;
import com.pragma.plazoleta.domain.api.IRestaurantServicePort;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class RestaurantUseCase implements IRestaurantServicePort {
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IUserRoleValidationPort userRoleValidationPort;

    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?\\d{1,13}$");
    private static final Pattern NAME_PATTERN = Pattern.compile(".*[a-zA-Z].*");

    @Override
    public Restaurant createRestaurant(Restaurant restaurant, String role) {
        validateAdminRole(role);
        validateRequiredFields(restaurant);
        validatePhone(restaurant.getPhone());
        validateName(restaurant.getName());
        validateOwnerRole(restaurant.getOwnerId());
        validateUniqueNit(restaurant.getNit());
        validateUniqueName(restaurant.getName());
        return restaurantPersistencePort.save(restaurant);
    }

    @Override
    public Optional<Restaurant> getById(String id) {
        return restaurantPersistencePort.findById(id);
    }

    private void validateRequiredFields(Restaurant r) {
        if (isBlank(r.getName())) throw new DomainException("Name is required");
        if (r.getNit() <= 0) throw new DomainException("NIT is required and must be positive");
        if (isBlank(r.getAddress())) throw new DomainException("Address is required");
        if (isBlank(r.getPhone())) throw new DomainException("Phone is required");
        if (isBlank(r.getLogoUrl())) throw new DomainException("Logo URL is required");
        if (isBlank(r.getOwnerId())) throw new DomainException("Owner ID is required");
    }

    private void validatePhone(String phone) {
        if (phone.length() > 13) {
            throw new DomainException("Phone must not exceed 13 characters");
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new DomainException("Phone must be numeric and may start with +");
        }
    }

    private void validateName(String name) {
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new DomainException("Name must contain at least one letter");
        }
    }

    private void validateOwnerRole(String ownerId) {
        String roleName = userRoleValidationPort.getRoleNameByUserId(ownerId);
        if (!"OWNER".equalsIgnoreCase(roleName)) {
            throw new DomainException("User must have OWNER role");
        }
    }

    private void validateUniqueNit(long nit) {
        if (restaurantPersistencePort.existsByNit(nit)) {
            throw new DomainException("NIT already exists");
        }
    }

    private void validateUniqueName(String name) {
        if (restaurantPersistencePort.existsByName(name)) {
            throw new DomainException("A restaurant with this name already exists");
        }
    }

    private void validateAdminRole(String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new DomainException("Only an ADMIN can create restaurants");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
} 