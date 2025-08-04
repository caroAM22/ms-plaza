package com.pragma.plazoleta.infrastructure.output.restclient;

import com.pragma.plazoleta.domain.model.Notification;
import com.pragma.plazoleta.domain.model.NotificationResult;
import com.pragma.plazoleta.domain.spi.IMessagePersistencePort;
import com.pragma.plazoleta.application.dto.request.NotificationRequest;
import com.pragma.plazoleta.application.dto.response.NotificationResponse;
import com.pragma.plazoleta.application.mapper.INotificationMapper;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageRestClientAdapter implements IMessagePersistencePort {

    private final MessageFeignClient messageFeignClient;
    private final INotificationMapper notificationMapper;

    @Override
    public Optional<NotificationResult> sendMessage(Notification notification) {
        try {
            NotificationRequest notificationRequest = notificationMapper.toNotificationRequest(notification);
            NotificationResponse notificationResponse = messageFeignClient.sendMessage(notificationRequest);
            return Optional.of(notificationMapper.toNotificationResult(notificationResponse));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }
} 