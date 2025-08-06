package com.pragma.plazoleta.application.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.pragma.plazoleta.application.dto.response.TraceabilityGroupedResponse;
import com.pragma.plazoleta.domain.model.TraceabilityGrouped;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ITraceabilityGroupedMapper {
    List<TraceabilityGrouped> toTraceabilityGroupedList(List<TraceabilityGroupedResponse> traceabilityGroupedResponseList);
    List<TraceabilityGroupedResponse> toTraceabilityGroupedResponseList(List<TraceabilityGrouped> traceabilityGroupedList);
}
