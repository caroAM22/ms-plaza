package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.response.OrderSummaryResponse;
import com.pragma.plazoleta.domain.model.OrderSummary;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IOrderSummaryMapper {
    OrderSummaryResponse toOrderSummaryResponse(OrderSummary orderSummary);
    List<OrderSummaryResponse> toOrderSummaryResponseList(List<OrderSummary> orderSummaryList);
    List<OrderSummary> toOrderSummaryList(List<OrderSummaryResponse> orderSummaryResponseList);
} 