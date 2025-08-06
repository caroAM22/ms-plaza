package com.pragma.plazoleta.infrastructure.output.rest.client;

import com.pragma.plazoleta.application.dto.request.TraceabilityRequest;
import com.pragma.plazoleta.application.dto.response.EmployeeAverageTimeResponse;
import com.pragma.plazoleta.application.dto.response.OrderSummaryResponse;
import com.pragma.plazoleta.application.dto.response.TraceabilityGroupedResponse;
import com.pragma.plazoleta.application.dto.response.TraceabilityResponse;
import com.pragma.plazoleta.infrastructure.output.rest.FeignClientConfig;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-trace", url = "${ms-trace.url}", configuration = FeignClientConfig.class)
public interface TraceFeignClient {
    @PostMapping("/traceability")
    TraceabilityResponse createTrace(@RequestBody TraceabilityRequest traceabilityRequest);

    @GetMapping("/traceability/restaurant/{restaurantId}")
    List<OrderSummaryResponse> getTraceByRestaurantId(@PathVariable("restaurantId") String restaurantId);

    @GetMapping("/traceability/order/{orderId}")
    List<TraceabilityResponse> getTraceOrderById(@PathVariable("orderId") String orderId);

    @GetMapping("/traceability/employee/{employeeId}")
    List<EmployeeAverageTimeResponse> getTraceEmployeeById(@PathVariable("employeeId") String employeeId);

    @GetMapping("/traceability/client/{clientId}")
    List<TraceabilityGroupedResponse> getTraceClientById(@PathVariable("clientId") String clientId);
} 