package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.request.NotificationRequest;
import com.pragma.plazoleta.application.dto.response.NotificationResponse;
import com.pragma.plazoleta.domain.model.NotificationResult;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface INotificationMapper {
    NotificationResponse toNotificationResponse(NotificationResult notificationResult);
    NotificationRequest toNotificationRequest(com.pragma.plazoleta.domain.model.Notification notification);
    NotificationResult toNotificationResult(NotificationResponse notificationResponse);
} 