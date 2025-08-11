package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.exception.DomainException;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.model.DomainPage;
import com.pragma.plazoleta.domain.model.EmployeeAverageTime;
import com.pragma.plazoleta.domain.model.OrderSummary;
import com.pragma.plazoleta.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazoleta.domain.spi.ISecurityContextPort;
import com.pragma.plazoleta.domain.spi.ITraceCommunicationPort;
import com.pragma.plazoleta.domain.spi.IUserRoleValidationPort;
import com.pragma.plazoleta.domain.utils.Constants;
import com.pragma.plazoleta.domain.api.IRestaurantServicePort;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import static com.pragma.plazoleta.domain.utils.Constants.NAME_PATTERN_REQUIRED;
import static com.pragma.plazoleta.domain.utils.Constants.PHONE_PATTERN_REQUIRED;

@RequiredArgsConstructor
public class RestaurantUseCase implements IRestaurantServicePort {
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IUserRoleValidationPort userRoleValidationPort;
    private final ISecurityContextPort securityContextPort;
    private final ITraceCommunicationPort traceCommunicationPort;

    @Override
    public Restaurant createRestaurant(Restaurant restaurant) {
        validateAdminRole();
        validateRequiredFields(restaurant);
        validatePhone(restaurant.getPhone());
        validateName(restaurant.getName());
        validateOwnerRole(restaurant.getOwnerId());
        validateUniqueNit(restaurant.getNit());
        validateUniqueName(restaurant.getName());
        return restaurantPersistencePort.save(restaurant);
    }

    @Override
    public Restaurant getRestaurantById(UUID id) {
        return restaurantPersistencePort.findById(id)
                .orElseThrow(() -> new DomainException("Restaurant not found"));
    }

    @Override
    public DomainPage<Restaurant> getAllRestaurants(int page, int size) {
        return restaurantPersistencePort.findAll(page, size);
    }

    @Override
    public boolean existsById(UUID id) {
        return restaurantPersistencePort.existsById(id);
    }

    @Override
    public List<OrderSummary> getRestaurantOrdersSummary(UUID restaurantId) {
        validateRoleAndRestaurantOwner(restaurantId);
        return traceCommunicationPort.getTraceByRestaurantId(restaurantId);
    }

    @Override
    public List<EmployeeAverageTime> getRestaurantEmployeesRanking(UUID restaurantId) {
        validateRoleAndRestaurantOwner(restaurantId);
        return traceCommunicationPort.getEmployeeAverageTime(restaurantId);
    }

    private void validateRoleAndRestaurantOwner(UUID restaurantId) {
        if (!"OWNER".equalsIgnoreCase(securityContextPort.getRoleOfUserAutenticated()) || !getRestaurantById(restaurantId).getOwnerId().equals(securityContextPort.getUserIdOfUserAutenticated())) {
            throw new DomainException("You are not the owner of this restaurant");
        }
    }

    private void validateRequiredFields(Restaurant r) {
        if (isBlank(r.getName())) throw new DomainException("Name is required");
        if (r.getNit() <= 0) throw new DomainException("NIT is required and must be positive");
        if (isBlank(r.getAddress())) throw new DomainException("Address is required");
        if (isBlank(r.getPhone())) throw new DomainException("Phone is required");
        if (isBlank(r.getLogoUrl())) throw new DomainException("Logo URL is required");
        if (r.getOwnerId() == null) throw new DomainException("Owner ID is required");
    }

    private void validatePhone(String phone) {
        if (phone.length() > Constants.MAXIMUM_PHONE_LENGTH) {
            throw new DomainException("Phone must not exceed 13 characters");
        }
        if (!PHONE_PATTERN_REQUIRED.matcher(phone).matches()) {
            throw new DomainException("Phone must be numeric and may start with +");
        }
    }

    private void validateName(String name) {
        if (!NAME_PATTERN_REQUIRED.matcher(name).matches()) {
            throw new DomainException("Name must contain at least one letter");
        }
    }

    private void validateOwnerRole(UUID ownerId) {
        String roleName = userRoleValidationPort.getRoleNameByUserId(ownerId)
                .orElseThrow(() -> new DomainException("User not found or has no role"));
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

    private void validateAdminRole() {
        String role = securityContextPort.getRoleOfUserAutenticated();
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new DomainException("Only an ADMIN can create restaurants");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
} 