package com.pragma.plazoleta.application.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.pragma.plazoleta.application.dto.request.TraceabilityRequest;
import com.pragma.plazoleta.application.dto.response.TraceabilityResponse;
import com.pragma.plazoleta.domain.model.Traceability;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ITraceabilityMapper {
    Traceability toTraceability(TraceabilityRequest traceabilityRequest);
    Traceability toTraceability(TraceabilityResponse traceabilityResponse);
    TraceabilityRequest toTraceabilityRequest(Traceability traceability);
    TraceabilityResponse toTraceabilityResponse(Traceability traceability);
    List<TraceabilityResponse> toTraceabilityResponseList(List<Traceability> traceabilityList);
    List<Traceability> toTraceabilityList(List<TraceabilityResponse> traceabilityResponseList);
} 