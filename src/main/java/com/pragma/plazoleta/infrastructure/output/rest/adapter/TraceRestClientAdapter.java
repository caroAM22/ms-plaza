package com.pragma.plazoleta.infrastructure.output.rest.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.pragma.plazoleta.application.mapper.IEmployeeAverageTimeMapper;
import com.pragma.plazoleta.application.mapper.IOrderSummaryMapper;
import com.pragma.plazoleta.application.mapper.ITraceabilityGroupedMapper;
import com.pragma.plazoleta.application.mapper.ITraceabilityMapper;
import com.pragma.plazoleta.domain.model.EmployeeAverageTime;
import com.pragma.plazoleta.domain.model.OrderSummary;
import com.pragma.plazoleta.domain.model.Traceability;
import com.pragma.plazoleta.domain.model.TraceabilityGrouped;
import com.pragma.plazoleta.domain.spi.ITraceCommunicationPort;
import com.pragma.plazoleta.infrastructure.output.rest.client.TraceFeignClient;
import com.pragma.plazoleta.application.dto.request.TraceabilityRequest;
import com.pragma.plazoleta.application.dto.response.EmployeeAverageTimeResponse;
import com.pragma.plazoleta.application.dto.response.OrderSummaryResponse;
import com.pragma.plazoleta.application.dto.response.TraceabilityGroupedResponse;
import com.pragma.plazoleta.application.dto.response.TraceabilityResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TraceRestClientAdapter implements ITraceCommunicationPort {

    private final TraceFeignClient traceFeignClient;
    private final ITraceabilityMapper traceabilityMapper;
    private final ITraceabilityGroupedMapper traceabilityGroupedMapper;
    private final IOrderSummaryMapper orderSummaryMapper;
    private final IEmployeeAverageTimeMapper employeeAverageTimeMapper;

    @Override
    public Optional<Traceability> createTrace(Traceability traceability) {
        TraceabilityRequest traceabilityRequest = traceabilityMapper.toTraceabilityRequest(traceability);
        TraceabilityResponse traceabilityResponse = traceFeignClient.createTrace(traceabilityRequest);
        return Optional.of(traceabilityMapper.toTraceability(traceabilityResponse));
    }

    @Override
    public List<OrderSummary> getTraceByRestaurantId(UUID restaurantId) {
        List<OrderSummaryResponse> orderSummaryResponseList = traceFeignClient.getTraceByRestaurantId(restaurantId.toString());
        return orderSummaryMapper.toOrderSummaryList(orderSummaryResponseList);
    }

    @Override
    public List<Traceability> getTraceByOrderId(UUID id) {
        List<TraceabilityResponse> traceabilityResponseList = traceFeignClient.getTraceOrderById(id.toString());
        return traceabilityMapper.toTraceabilityList(traceabilityResponseList);
    }

    @Override
    public List<EmployeeAverageTime> getEmployeeAverageTime(UUID id) {
        List<EmployeeAverageTimeResponse> employeeAverageTimeResponseList = traceFeignClient.getTraceEmployeeById(id.toString());
        return employeeAverageTimeMapper.toEmployeeAverageTimeList(employeeAverageTimeResponseList);
    }

    @Override
    public List<TraceabilityGrouped> getTraceByClientId(UUID id) {
        List<TraceabilityGroupedResponse> traceabilityResponseList = traceFeignClient.getTraceClientById(id.toString());
        return traceabilityGroupedMapper.toTraceabilityGroupedList(traceabilityResponseList);
    }
}
