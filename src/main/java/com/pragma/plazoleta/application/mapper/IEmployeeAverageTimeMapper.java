package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.response.EmployeeAverageTimeResponse;
import com.pragma.plazoleta.domain.model.EmployeeAverageTime;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.time.Duration;
import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IEmployeeAverageTimeMapper {
    EmployeeAverageTimeResponse toEmployeeAverageTimeResponse(EmployeeAverageTime employeeAverageTime);
    List<EmployeeAverageTimeResponse> toEmployeeAverageTimeResponseList(List<EmployeeAverageTime> employeeAverageTimeList);
    List<EmployeeAverageTime> toEmployeeAverageTimeList(List<EmployeeAverageTimeResponse> employeeAverageTimeResponseList);
    
    default String durationToString(Duration duration) {
        return duration != null ? duration.toString() : null;
    }
} 