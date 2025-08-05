package com.pragma.plazoleta.infrastructure.output.rest.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.pragma.plazoleta.application.mapper.ITraceabilityMapper;
import com.pragma.plazoleta.domain.model.Traceability;
import com.pragma.plazoleta.domain.spi.ITracePersistencePort;
import com.pragma.plazoleta.infrastructure.output.rest.client.TraceFeignClient;
import com.pragma.plazoleta.application.dto.request.TraceabilityRequest;
import com.pragma.plazoleta.application.dto.response.TraceabilityResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TraceRestClientAdapter implements ITracePersistencePort {

    private final TraceFeignClient traceFeignClient;
    private final ITraceabilityMapper traceabilityMapper;

    @Override
    public Optional<Traceability> createTrace(Traceability traceability) {
        TraceabilityRequest traceabilityRequest = traceabilityMapper.toTraceabilityRequest(traceability);
        TraceabilityResponse traceabilityResponse = traceFeignClient.createTrace(traceabilityRequest);
        return Optional.of(traceabilityMapper.toTraceability(traceabilityResponse));
    }

    @Override
    public List<Traceability> getTrace() {
        List<TraceabilityResponse> traceabilityResponseList = traceFeignClient.getTrace();
        return traceabilityMapper.toTraceabilityList(traceabilityResponseList);
    }

    @Override
    public List<Traceability> getTraceByOrderId(UUID id) {
        List<TraceabilityResponse> traceabilityResponseList = traceFeignClient.getTraceOrderById(id.toString());
        return traceabilityMapper.toTraceabilityList(traceabilityResponseList);
    }

    @Override
    public List<Traceability> getTraceByEmployeeId(UUID id) {
        List<TraceabilityResponse> traceabilityResponseList = traceFeignClient.getTraceEmployeeById(id.toString());
        return traceabilityMapper.toTraceabilityList(traceabilityResponseList);
    }

    @Override
    public List<Traceability> getTraceByClientId(UUID id) {
        List<TraceabilityResponse> traceabilityResponseList = traceFeignClient.getTraceClientById(id.toString());
        return traceabilityMapper.toTraceabilityList(traceabilityResponseList);
    }
}
