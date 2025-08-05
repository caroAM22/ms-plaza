package com.pragma.plazoleta.infrastructure.output.rest.client;

import com.pragma.plazoleta.application.dto.response.NotificationResponse;
import com.pragma.plazoleta.infrastructure.output.rest.FeignClientConfig;
import com.pragma.plazoleta.application.dto.request.NotificationRequest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-notification", url = "${ms-notification.url}", configuration = FeignClientConfig.class)
public interface NotificationFeignClient {
    @PostMapping("/notification/send")
    NotificationResponse sendNotification(@RequestBody NotificationRequest notificationRequest);
} 