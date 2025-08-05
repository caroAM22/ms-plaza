package com.pragma.plazoleta.domain.spi;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.pragma.plazoleta.domain.model.Traceability;

public interface ITracePersistencePort {
    Optional<Traceability> createTrace(Traceability traceability);
    List<Traceability> getTrace();
    List<Traceability> getTraceByOrderId(UUID id);
    List<Traceability> getTraceByEmployeeId(UUID id);
    List<Traceability> getTraceByClientId(UUID id);
}
