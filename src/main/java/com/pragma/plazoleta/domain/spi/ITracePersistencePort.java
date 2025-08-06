package com.pragma.plazoleta.domain.spi;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.pragma.plazoleta.domain.model.EmployeeAverageTime;
import com.pragma.plazoleta.domain.model.OrderSummary;
import com.pragma.plazoleta.domain.model.Traceability;
import com.pragma.plazoleta.domain.model.TraceabilityGrouped;

public interface ITracePersistencePort {
    Optional<Traceability> createTrace(Traceability traceability);
    List<OrderSummary> getTraceByRestaurantId(UUID restaurantId);
    List<Traceability> getTraceByOrderId(UUID id);
    List<EmployeeAverageTime> getEmployeeAverageTime(UUID id);
    List<TraceabilityGrouped> getTraceByClientId(UUID id);
}
