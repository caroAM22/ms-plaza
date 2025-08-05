package com.pragma.plazoleta.infrastructure.output.rest.adapter;

import com.pragma.plazoleta.domain.model.Notification;
import com.pragma.plazoleta.domain.model.NotificationResult;
import com.pragma.plazoleta.domain.spi.INotificationPersistencePort;
import com.pragma.plazoleta.infrastructure.output.rest.client.NotificationFeignClient;
import com.pragma.plazoleta.application.dto.request.NotificationRequest;
import com.pragma.plazoleta.application.dto.response.NotificationResponse;
import com.pragma.plazoleta.application.mapper.INotificationMapper;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationRestClientAdapter implements INotificationPersistencePort {

    private final NotificationFeignClient notificationFeignClient;
    private final INotificationMapper notificationMapper;

    @Override
    public Optional<NotificationResult> sendMessage(Notification notification) {
        try {
            NotificationRequest notificationRequest = notificationMapper.toNotificationRequest(notification);
            NotificationResponse notificationResponse = notificationFeignClient.sendNotification(notificationRequest);
            return Optional.of(notificationMapper.toNotificationResult(notificationResponse));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }
} 