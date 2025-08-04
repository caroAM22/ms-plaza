package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.Notification;
import com.pragma.plazoleta.domain.model.NotificationResult;

import java.util.Optional;

public interface IMessagePersistencePort {
    Optional<NotificationResult> sendMessage(Notification notification);
} 