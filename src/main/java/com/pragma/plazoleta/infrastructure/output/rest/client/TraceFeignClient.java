package com.pragma.plazoleta.infrastructure.output.rest.client;

import com.pragma.plazoleta.application.dto.request.TraceabilityRequest;
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

    @GetMapping("/traceability")
    List<TraceabilityResponse> getTrace();

    @GetMapping("/traceability/order/{id}")
    List<TraceabilityResponse> getTraceOrderById(@PathVariable("id") String id);

    @GetMapping("/traceability/employee/{id}")
    List<TraceabilityResponse> getTraceEmployeeById(@PathVariable("id") String id);

    @GetMapping("/traceability/client/{id}")
    List<TraceabilityResponse> getTraceClientById(@PathVariable("id") String id);

} 